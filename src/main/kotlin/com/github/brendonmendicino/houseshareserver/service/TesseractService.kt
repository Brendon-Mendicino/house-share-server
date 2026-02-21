package com.github.brendonmendicino.houseshareserver.service

import org.bytedeco.javacpp.FloatPointer
import org.bytedeco.javacpp.indexer.IntIndexer
import org.bytedeco.javacv.Java2DFrameUtils
import org.bytedeco.opencv.global.opencv_core.BORDER_CONSTANT
import org.bytedeco.opencv.global.opencv_core.CV_32F
import org.bytedeco.opencv.global.opencv_imgproc.*
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.MatVector
import org.bytedeco.opencv.opencv_core.Point
import org.bytedeco.opencv.opencv_core.Size
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.math.roundToLong


@Service
class TesseractService {
    companion object {
        val logger = LoggerFactory.getLogger(TesseractService::class.java)!!
    }

//    private fun decode(file: MultipartFile): Mat {
//        val bytes = file.bytes
//        val raw = Mat(1, bytes.size, CV_8U, BytePointer(*bytes))
//        return imdecode(raw, IMREAD_COLOR)
//    }

    private fun Mat.toByteArray(): ByteArray {
        val image = Java2DFrameUtils.toBufferedImage(this)
        val os = ByteArrayOutputStream()
        ImageIO.write(image, "jpg", os)
        return os.toByteArray()
    }

    private fun MatVector.iter() = sequence {
        for (i in 0..<size()) yield(get(i)!!)
    }

    private fun scaleContours(contour: Mat, origW: Int, origH: Int, resizedW: Int, resizedH: Int) {
        // 1. Calculate the scaling factors
        val scaleX = origW.toDouble() / resizedW
        val scaleY = origH.toDouble() / resizedH

        // 3. Create an indexer to access the raw data of the matrix
        // Contours are typically CV_32SC2 (Integer, 2 channels: x and y)
        val indexer = contour.createIndexer<IntIndexer>()

        // 4. Iterate through each point in the contour
        // contour.rows() usually represents the number of points
        for (j in 0..<contour.rows().toLong()) {
            // Get current coordinates (Channel 0 is X, Channel 1 is Y)

            val x: Int = indexer.get(j, 0, 0)
            val y: Int = indexer.get(j, 0, 1)

            // Apply scaling
            val newX = (x * scaleX).roundToLong().toInt()
            val newY = (y * scaleY).roundToLong().toInt()

            // Write back to the Mat
            indexer.put(j, 0, 0, newX)
            indexer.put(j, 0, 1, newY)
        }

        // Release the indexer to free memory references (good practice in JavaCV)
        indexer.release()
    }

    private fun toPoints(pts: Mat): List<Point> {
        // sort the points based on their x-coordinates
        val indexer = pts.createIndexer<IntIndexer>()

        val points = mutableListOf<Point>()

        for (i in 0 until pts.rows().toLong()) {
            val x = indexer.get(0, i, 0)
            val y = indexer.get(0, i, 1)
            points.add(Point(x, y))
        }

        return points
    }

    /**
     * Sort TL, TR, BR, BL using sum/diff heuristic (most robust for rectangles).
     */
    fun sortCornersTlTrBrBl(points: List<Point>): List<Point> {
        require(points.size == 4)
        val sumSorted = points.sortedBy { it.x() + it.y() }
        val diffSorted = points.sortedBy { it.y() - it.x() }

        val topLeft = sumSorted.first()
        val bottomRight = sumSorted.last()
        val topRight = diffSorted.first()
        val bottomLeft = diffSorted.last()

        return listOf(topLeft, topRight, bottomRight, bottomLeft)
    }

    /**
     * Performs a perspective warp that takes four corners and stretches them to the corners of the image.
     * x1,y1 represents the top left corner, 2 top right, going clockwise.
     * This method does not release/deallocate the input image Mat, call inputMat.release() after this method
     * if you don't plan on using the input more after this method.
     *
     * @param image The image to perform the stretch on
     * @return A stretched image mat.
     */
    private fun performPerspectiveWarp(image: Mat, pts: Mat) {
        val imageW = image.size().width()
        val imageH = image.size().height()
        val points = toPoints(pts)

        // Sort corners: TL, TR, BR, BL
        val sorted = sortCornersTlTrBrBl(points)

        val srcPts = FloatPointer(
            *sorted.flatMap { listOf(it.x(), it.y()) }.map { it.toFloat() }.toFloatArray()
        )

        val dstPts = FloatPointer(
            0f, 0f,
            imageW.toFloat(), 0f,
            imageW.toFloat(), imageH.toFloat(),
            0f, imageH.toFloat(),
        )

        //create matrices with width 2 to hold the x,y values, and 4 rows, to hold the 4 different corners.
        val src = Mat(Size(2, 4), CV_32F, srcPts)
        val dst = Mat(Size(2, 4), CV_32F, dstPts)

        val perspective = getPerspectiveTransform(src, dst)

        warpPerspective(image, image, perspective, Size(imageW, imageH))

        src.release()
        dst.release()
        srcPts.deallocate()
        dstPts.deallocate()
    }

    fun process(file: MultipartFile): ByteArray {
        val io = ByteArrayInputStream(file.bytes)
        val src = Java2DFrameUtils.toMat(ImageIO.read(io))

        val imageResize = Size(480, 640)
        val resized = Mat()
        resize(src, resized, imageResize)

        // Downscale image as finding receipt contour is more efficient on a small image
//        val morphed = Mat()
//        morphologyEx(src, morphed, MORPH_CLOSE, Mat(5, 5, CV_8U, Scalar(1.0)), null, 3, BORDER_CONSTANT, null)

        // Convert to Grayscale
        val gray = Mat()
        cvtColor(resized, gray, COLOR_BGR2GRAY)

        // Noise reduction (edge-preserving)
        val blurred = Mat()
        GaussianBlur(gray, blurred, Size(5, 5), 1.0)

        // Edge detection
        val canny = Mat()
        Canny(blurred, canny, 50.0, 200.0)

        // Improve edge detection
        val kernel = getStructuringElement(MORPH_RECT, Size(5, 5))
        val improved = Mat()
        dilate(canny, improved, kernel, null, 2, BORDER_CONSTANT, null)
        erode(improved, improved, kernel)

        // Finding contours
        val contours = MatVector()
        findContours(improved, contours, RETR_LIST, CHAIN_APPROX_SIMPLE)
        logger.warn("Contours found: ${contours.size()}")

        // Find the largest contour that has 4 vertices
        val contour = contours.iter()
            .sortedByDescending { contourArea(it) }
            .filter { contourArea(it) > 1000 }
            .map { contour ->
                val approx = Mat()
                val peri = arcLength(contour, true)
                approxPolyDP(contour, approx, 0.02 * peri, true)
                approx
            }
            .firstOrNull { approx -> approx.rows() == 4 }

//        drawContours(resized, MatVector(contour!!), -1, Scalar.GREEN, 10, LINE_8, null, INTER_MAX, null)
        if (contour != null) {
            performPerspectiveWarp(resized, contour)
        }

        return resized.toByteArray()
    }
}
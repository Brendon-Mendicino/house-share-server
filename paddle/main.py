import io

import numpy as np
import uvicorn
from PIL import Image
from fastapi import FastAPI, HTTPException, UploadFile, File
from paddleocr import PaddleOCR

app = FastAPI()

# TODO: externalize the configuration
ocr = PaddleOCR(
    use_doc_orientation_classify=True, use_doc_unwarping=True
)  # text image preprocessing + text detection + textline orientation classification + text recognition


# ocr = PaddleOCR(
#     text_detection_model_name="PP-OCRv5_mobile_det",
#     text_recognition_model_name="PP-OCRv5_mobile_rec",
#     use_doc_orientation_classify=False,
#     use_doc_unwarping=False,
#     use_textline_orientation=False,
# )


def process_json(json: dict) -> list[dict]:
    data = json.get("res", {})

    texts = data.get("rec_texts", [])
    scores = data.get("rec_scores", [])
    boxes = data.get("rec_boxes", [])

    regions = []

    for text, score, box in zip(texts, scores, boxes):
        # TODO: make this var configurable
        if score < 0.3:
            continue

        stripped = text.strip()
        print(box)
        x1, y1, x2, y2 = box[:4]

        regions.append(
            {
                "text": stripped,
                "bbox": {
                    "x1": x1,
                    "y1": y1,
                    "x2": x2,
                    "y2": y2,
                },
                "score": score,
            }
        )

    return regions


@app.get("/")
async def root():
    return {"status": "up", "service": "paddle"}


@app.post("/ocr")
async def run_ocr(file: UploadFile = File(...)):
    if file.content_type not in ["image/jpeg", "image/jpg", "image/png"]:
        raise HTTPException(
            status_code=400,
            detail=f"FIle Content-Type not supported: {file.content_type=}",
        )

    contents = await file.read()
    image = Image.open(io.BytesIO(contents))
    img_np = np.array(image)

    result = ocr.predict(img_np)

    output = []

    for res in result:
        json = res.json

        text = process_json(json)
        output.append(text)

    return {"lines": output}


if __name__ == "__main__":
    uvicorn.run(app=app, host="0.0.0.0")

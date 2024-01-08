from ultralytics import YOLO

import time

model = YOLO("yolov8x.pt")

result = model.predict(source='./2.jpg', save=True, save_txt=True, classes=0)
print('=======================')
print(result[0].boxes.shape[0])
#  time.sleep(1000000000)

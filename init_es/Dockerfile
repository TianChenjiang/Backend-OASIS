FROM python:alpine

WORKDIR /usr/app

COPY ./requirements.txt ./

RUN pip install -i https://pypi.tuna.tsinghua.edu.cn/simple --no-cache-dir -r requirements.txt

COPY ./ ./

CMD ["python", "import_es.py"]
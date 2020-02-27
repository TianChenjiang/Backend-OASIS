from pymongo import MongoClient
import json

client = MongoClient('mongodb://localhost:27017/')
db = client['se3']
collection = db['papers']


def import_data(json_name):
    with open(json_name, 'r') as f:
        json_data = json.load(f)
    result = collection.insert_many(json_data)
    assert len(result.inserted_ids) == len(json_data)


if __name__ == '__main__':
    import_data('with link ICSE.json')
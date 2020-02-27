import pandas as pd
import json


def add_attr(json_name: str, csv_name: str):
    with open(json_name, 'r') as f:
        json_data = json.load(f)
    csv_df = pd.read_csv(csv_name)

    assert len(json_data) == len(csv_df)
    for i, link in enumerate(csv_df['PDF Link']):
        json_data[i]['link'] = link

    with open('with link ' + json_name, 'w') as f:
        json.dump(json_data, f)


if __name__ == '__main__':
    add_attr('ICSE.json', 'ICSE-clean.csv')

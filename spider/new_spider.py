import csv
import json
import re

import pandas as pd
from tqdm import tqdm
import requests

# from pymongo import MongoClient

# ase
# https://ieeexplore.ieee.org/search/searchresult.jsp?action=search&matchBoolean=true&queryText=(%22All%20Metadata%22:ASE)&highlight=true&returnType=SEARCH&refinements=ContentType:Conferences&refinements=PublicationTitle:2019%2034th%20IEEE%2FACM%20International%20Conference%20on%20Automated%20Software%20Engineering%20(ASE)&refinements=PublicationTitle:2017%2032nd%20IEEE%2FACM%20International%20Conference%20on%20Automated%20Software%20Engineering%20(ASE)&refinements=PublicationTitle:2011%2026th%20IEEE%2FACM%20International%20Conference%20on%20Automated%20Software%20Engineering%20(ASE%202011)&refinements=PublicationTitle:2015%2030th%20IEEE%2FACM%20International%20Conference%20on%20Automated%20Software%20Engineering%20(ASE)&refinements=PublicationTitle:2016%2031st%20IEEE%2FACM%20International%20Conference%20on%20Automated%20Software%20Engineering%20(ASE)&refinements=PublicationTitle:2008%2023rd%20IEEE%2FACM%20International%20Conference%20on%20Automated%20Software%20Engineering&refinements=PublicationTitle:2009%20IEEE%2FACM%20International%20Conference%20on%20Automated%20Software%20Engineering&refinements=PublicationTitle:2013%2028th%20IEEE%2FACM%20International%20Conference%20on%20Automated%20Software%20Engineering%20(ASE)&refinements=PublicationTitle:Proceedings.%2019th%20International%20Conference%20on%20Automated%20Software%20Engineering,%202004.&refinements=PublicationTitle:21st%20IEEE%2FACM%20International%20Conference%20on%20Automated%20Software%20Engineering%20(ASE%2706)&refinements=PublicationTitle:Proceedings%2016th%20Annual%20International%20Conference%20on%20Automated%20Software%20Engineering%20(ASE%202001)&refinements=PublicationTitle:18th%20IEEE%20International%20Conference%20on%20Automated%20Software%20Engineering,%202003.%20Proceedings.&refinements=PublicationTitle:Proceedings%2017th%20IEEE%20International%20Conference%20on%20Automated%20Software%20Engineering,&refinements=PublicationTitle:Proceedings%20ASE%202000.%20Fifteenth%20IEEE%20International%20Conference%20on%20Automated%20Software%20Engineering&ranges=2000_2020_Year&returnFacets=ALL

# icse
# https://ieeexplore.ieee.org/search/searchresult.jsp?queryText=ICSE&highlight=true&returnType=SEARCH&returnFacets=ALL&refinements=PublicationTitle:2015%20IEEE%2FACM%2037th%20IEEE%20International%20Conference%20on%20Software%20Engineering&refinements=PublicationTitle:2018%20IEEE%2FACM%2040th%20International%20Conference%20on%20Software%20Engineering%20(ICSE)&refinements=PublicationTitle:2019%20IEEE%2FACM%2041st%20International%20Conference%20on%20Software%20Engineering%20(ICSE)&refinements=PublicationTitle:2016%20IEEE%2FACM%2038th%20International%20Conference%20on%20Software%20Engineering%20(ICSE)&refinements=PublicationTitle:2017%20IEEE%2FACM%2039th%20International%20Conference%20on%20Software%20Engineering%20(ICSE)&refinements=PublicationTitle:2013%2035th%20International%20Conference%20on%20Software%20Engineering%20(ICSE)&refinements=PublicationTitle:2012%2034th%20International%20Conference%20on%20Software%20Engineering%20(ICSE)&refinements=PublicationTitle:2011%2033rd%20International%20Conference%20on%20Software%20Engineering%20(ICSE)

USER_AGENT = 'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 ' \
             'Safari/537.36 '

# client = MongoClient('mongodb://localhost:27017/')
# db = client['se3']
# collection = db['papers']


def get_keywords(content):
    if 'keywords' not in content:
        return None
    for kds in content['keywords']:
        if kds["type"] == 'IEEE Keywords':
            return kds["kwd"]
    return None


def get_reference(link_num):
    url = "https://ieeexplore.ieee.org/rest/document/" + link_num + "/references"
    headers = {"Connection": "close", "Accept": "application/json, text/plain, */*", "cache-http-response": "true",
               "User-Agent": "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3377.1 Safari/537.36",
               "Referer": "https://ieeexplore.ieee.org/document/" + link_num + "/references",
               "Accept-Encoding": "gzip, deflate",
               "Accept-Language": "zh-CN,zh;q=0.9,en;q=0.8"}
    res = requests.get(url, headers=headers)
    content = json.loads(res.text)
    return content.get('references', None)


def ieee_info(link_num):
    paper_url = "https://ieeexplore.ieee.org/document/" + link_num
    headers = {'User-Agent': USER_AGENT}
    res = requests.get(paper_url)

    pattern = re.compile('metadata={.*};')
    content = json.loads(pattern.search(res.text).group()[9:-1])

    required = ['title', 'authors', 'abstract', 'publicationTitle', 'doi', 'publicationYear', 'metrics']
    paper = {k: content.get(k, None) for k in required}

    paper['keywords'] = get_keywords(content)
    paper['references'] = get_reference(link_num)

    return paper


def convert_link_num(x):
    pdf_link = x['PDF Link']
    link_num = pdf_link[pdf_link.rfind('=') + 1:]
    return link_num


def spider(csv_path, conference_name):
    result = []
    df = pd.read_csv(csv_path)
    links = df.apply(convert_link_num, axis=1)

    i = 0
    for link_num in tqdm(links):
        # try:
        #     paper = ieee_info(link_num)
        # except:
        #     with open('error.txt', 'a') as f:
        #         f.write('{}\n'.format(i))
        #         i += 1
        #         continue
        paper = ieee_info(link_num)

        paper['conferenceName'] = conference_name

        result.append(paper)
        # collection.insert_one(paper)
        i += 1

    with open('{}.json'.format(conference_name), 'w') as f:
        json.dump(result, f)


if __name__ == '__main__':
    spider('ICSE-clean.csv', 'ICSE')

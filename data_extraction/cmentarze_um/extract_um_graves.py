import requests
import time
import re
import csv
import time
from xml.dom import minidom
from statistics import mean


class CEMETARIES:
    STARE_POWAZKI = 2
    EWANGELICKI = 5
    POLNOCNY = 100
    POWAZKI_WOJSKOWE = 101


LIST_REQUEST_URL = 'http://mapa.um.warszawa.pl/mapaApp1/PowazkiSzukajLista'
BASE_LIST_PARAMS = {'sEcho': 1,
                    'iColumns': 3,
                    'sColumns': '',
                    'iDisplayStart': 0,
                    'iDisplayLength': 100,
                    'mDataProp_0': 0,
                    'mDataProp_1': 1,
                    'mDataProp_2': 2,
                    'POM_LOK_ID': 2,
                    'POH_NAZWISKO': '',
                    'POH_IMIE': 'Stefania',
                    '_': 1590003264415}

DETAILS_REQUEST_URL = 'http://mapa.um.warszawa.pl/mapaApp1/PowazkiSzukajByPohId'

MOCK = False


def find_grave_id(first, last, year, cemetery):
    if MOCK:
        return "40002433"
    params = BASE_LIST_PARAMS.copy()
    params['POH_NAZWISKO'] = last
    params['POH_IMIE'] = first
    params['POM_LOK_ID'] = cemetery
    params['_'] = int(time.time() * 1000)
    # print(params)
    # sending get request and saving the response as response object
    r = requests.get(LIST_REQUEST_URL, params)
    # print(r)
    data = r.json()
    res_rows = data['aaData']
    for row in res_rows:
        info = row[1]
        match = re.search(r", rok (\d{4})$", info)
        if match:
            rok = match.group(1)
            if year == int(rok):
                return row[0]
        else:
            print(f'Got info with no year for {first} {last}: "{info}"; assuming it is ok.')
            return row[0]
    return None


def get_grave_details_by_id(id):
    params = {'poh_id': id}
    r = requests.get(DETAILS_REQUEST_URL, params)
    data = r.text
    # print(f'Got data from request: {data}')
    return parse_grave_details(data)


XML_SAMPLE = '''<?xml version="1.0" encoding="UTF-8"?>
<xDoc>
<foi>
<POM_LOK_ID>2</POM_LOK_ID>
<POH_ID>40002124</POH_ID>
<POH_POMNIK_ID>40003806</POH_POMNIK_ID>
<POH_NAZWISKO>SIERPIŃSKI</POH_NAZWISKO>
<POH_IMIE>WACŁAW</POH_IMIE>
<KWATERA>ALEJA ZASŁUŻONYCH</KWATERA>
<POH_SMY>1969</POH_SMY>
<POH_SMM>10</POH_SMM>
<POH_SMD>21</POH_SMD>
<POM_RZAD>1</POM_RZAD>
<POM_MIEJSCE>153</POM_MIEJSCE>
<POM_FOTO1>9ae1b29f235f4b03bac34960dc2e339b.jpg</POM_FOTO1>
<POM_FOTO2>2cf1db7340ce48b1b78cd1c99f17a2aa.jpg</POM_FOTO2>
<POM_FOTO3>49184fb48b5e4e5f821602152706dead.jpg</POM_FOTO3>
<shape><gtype>3</gtype><srid>2178</srid><sdo_elem_info>1,1003,1</sdo_elem_info><sdo_ordinates>7498569.2525,5790973.6511,7498567.8953,5790973.7971,7498567.6993,5790971.2211,7498569.0567,5790971.0733,7498569.2525,5790973.6511</sdo_ordinates><sdo_point></sdo_point></shape>
</foi>
</xDoc>
'''


def getText(nodelist):
    rc = []
    for node in nodelist:
        if node.nodeType == node.TEXT_NODE:
            rc.append(node.data)
    return ''.join(rc)


def extract_field_text(xml_node, sub_node_name):
    return getText(xml_node.getElementsByTagName(sub_node_name)[0].childNodes)


def parse_grave_details(xml_details):
    res = {}
    xmldoc = minidom.parseString(xml_details)
    attr_map = {'cemetery': 'POM_LOK_ID',
                'first_name': 'POH_IMIE',
                'last_name': 'POH_NAZWISKO',
                'rok_smierci': 'POH_SMY',
                'kwatera': 'KWATERA',
                'rzad': 'POM_RZAD',
                'miejsce': 'POM_MIEJSCE'}
    for (attr, xmlattr) in attr_map.items():
        res[attr] = extract_field_text(xmldoc, xmlattr)
    coords = extract_field_text(xmldoc, 'sdo_ordinates')
    crd_arr = list(map(lambda x: float(x), coords.split(',')))
    res['x'] = mean(crd_arr[::2])
    res['y'] = mean(crd_arr[1::2])
    res['coords'] = coords
    return res


def build_grave_info(first, last, death_year, cemetery):
    grave_id = find_grave_id(first, last, death_year, cemetery)
    if grave_id:
        return get_grave_details_by_id(grave_id)
    else:
        return None


def prepare_input(file_name, cemetery):
    res = {}
    with open(file_name) as csv_file:
        input_reader = csv.DictReader(csv_file)
        for row in input_reader:
            name = row['name_in_native_language']
            nsplit = name.split(' ')
            fname = nsplit[0]
            lname = nsplit[len(nsplit) - 1]
            dd = int(row['deathDate'][0:4])
            res[row['person']] = {'first': fname, 'last': lname, 'death_year': dd}
    return list(res.values())


def write_output(result_rows, output_file):
    with open(output_file, 'w') as csvfile:
        fieldnames = [
            'cemetery', 'first_name', 'last_name', 'rok_smierci', 'kwatera', 'rzad', 'miejsce', 'x', 'y', 'coords'
        ]
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(result_rows)


def process_cemetery(input_file, output_file, cemetery_code):
    input=prepare_input(input_file, cemetery_code)
    res = []
    row=0
    for entry in input:
        e = build_grave_info(entry['first'], entry['last'], entry['death_year'], cemetery_code)
        if e:
            res.append(e)
        else:
            print(f'Failed to find data for {entry}')
        row+=1
        if row%5 ==0:
            print(f'Processsed {row} rows ({entry}->{e})')
        time.sleep(0.3)

    write_output(res, output_file)
    return len(res)


def main():
    input_files = [
        {'input': 'people_powazki.csv', 'output': 'graves_powazki.csv', 'cemetery': CEMETARIES.STARE_POWAZKI},
        {'input': 'people_wojskowy.csv', 'output': 'graves_wojskowy.csv', 'cemetery': CEMETARIES.POWAZKI_WOJSKOWE},
        {'input': 'people_polnocny.csv', 'output': 'graves_polnocny.csv', 'cemetery': CEMETARIES.POLNOCNY},
        {'input': 'people_ewangelicki.csv', 'output': 'graves_ewangelicki.csv', 'cemetery': CEMETARIES.EWANGELICKI}
    ]
    for cemetery in input_files:
        print(f'Preparing {cemetery}')
        count=process_cemetery(cemetery['input'], cemetery['output'], cemetery['cemetery'])
        print(f'Processed successfully, got {count} graves')
    exit(0)


if __name__ == "__main__":
    main()

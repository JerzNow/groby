import csv
import re
from collections import defaultdict
from functools import reduce


class StreetPersonInfo:

    def __init__(self, n1, n2):
        self.main_name = n1
        self.sec_name = normalize_name_extra(n2)
        self.main_name_nom = ""
        self.sec_name_nom = ""
        self.known_sec_names = [n2]
        self.count = 0

    def add_sec_name_version(self, sec_name):
        self.known_sec_names.append(sec_name)

    def coalesce(self, other_person):
        self.known_sec_names = self.known_sec_names + other_person.known_sec_names

    def occurences(self):
        return len(self.known_sec_names)

    def is_abbreviation(self):
        return len(self.sec_name.replace('.', '')) <= 2 and self.sec_name.endswith('.')

    def matches_abbreviation(self, abbreviation):
        a = abbreviation.replace('.', '')
        return self.sec_name.startswith(a)

    def wiki_id(self):
        sname=self.sec_name_nom.split(' ')[0]
        mname=self.main_name_nom
        return sname+' '+mname


    def __str__(self):
        return f'StreetPersonInfo[{self.main_name} {self.sec_name} [{self.sec_name_nom} {self.main_name_nom}]({len(self.known_sec_names)} representations)]'


def normalize_name_extra(name):
    prefixes = [
        "plac ", "pl.", "Osiedle", "Rondo", "al.", "aleja", "pasaż", "Most", "skwer", "park", "deptak",
        "im.", "imienia",
        "św.", "Świętego", "Świętej",
        "bł.", "Błog.", "Błogosławionego", "Błogosławionej",
        "ks.", "kś.", "Księdza", "kanonika", "kan.", "Prałata", "prał.", "Infułata", "inf.",
        "Prymasa", "prym.", "Tysiąclecia", "Kardynała", "kard.",
        "Biskupa", "bp.", "bp ", "bpa ", "bpa. ", "abp.", "abp ", "abpa ", "Arcybiskupa",
        "Ojca", "o.", "Siostry", "s. ",

        "Podharcmistrza", "Harcmistrz ", "Harcmistrza", "harcm.", "hm.", "hm ", "hr.", "Druha", "dh.", "harcerza",
        "harc.",
        "gen.", "Generała", "Generał ", "Bryg.", "brygady ", "broni ", "dyw.", "dywizji "
                                                                               "Marszałka", "marsz.",
        "Hetmana", "hetm.",
        "Admirała", "adm.", "Komandora", "kmdr.", "kmdr ", "Kmdra ", "Wiceadmirała", "Kontradmirała", "kadm.", "floty",
        "prof.", "Profesora", "Profesor ", "doc.", "dr.", "dr ", "dra ", "dr-a", "doktora", "doktor ",
        "lekarza ", "lek.", "n. ", "med.",
        "inż.",

        "Rotmistrza", "rtm.", "rotm.", "rot.",
        "mjr.", "Majora", "mjr ", "mjra ", "płk.", "płk ", "płka ", "Pułkownika", "ppłk.", "ppłk ", "ppłka ",
        "Podpułkownika", "dypl.",
        "kpt.", "kpt ", "Kapitana", "żeglugi wielkiej",
        "por.", "Porucznika",
        "ppor.", "ppr.", "P.por.", "podporucznika", "plut.", "st. sierż.", "sierż.", "kpr.", "kapr.", "kaprala ",
        "podch.",
        "pchor.", "chor.",
        "Plutonowego",
        "Prezydenta", "premiera", "RP ", "Polski", "Rzeczypospolitej Polskiej",
        "pilota", "pil.", "pil ", "Ułana", "Studenta",
        "inż.",
        "prof.", "Profesora", "Profesor ", "doc.", "dr.", "dr ", "dra ", "dr-a", "doktora", "doktor ",
        "Rabina ", "Powstańca", "Przeora", "Posła", "Przewodnika", "Pastora", "Partyzanta",
        "Senatora ", "Senator ", "Sołtysa", "Pierwszej damy", "Olimpijczyka", "Metropolity", "mecenasa"

                                                                                             "Nadleśniczego", "Wójta",
        "insp.", "rektora", "Astronoma", "kanc.", "arch.", "adw.", "Podkomisarza", "sędziego",
        "kpl.", "Kapelana", "Hrabiego", "Burmistrza"

    ]
    if re.fullmatch(r'([0-9]+|św)\.*', name, flags=re.IGNORECASE):
        return ''
    for prefix in prefixes:
        if name.lower().startswith(prefix.lower()):
            prefix = re.sub(r'\.', r'\.', prefix)
            name = re.sub('^' + prefix + r' *', '', name, flags=re.IGNORECASE)
    return name.strip()




def build_names_flexion_maps():
    print('Strarting building flex maps...')
    polimorf_file_name = 'PoliMorf-0.6.7.tab'
    first_name_dict = {}
    last_name_dict = {}
    with open(polimorf_file_name) as polimorf_file:
        for line in polimorf_file:
            entries = line.strip().split('\t')
            if len(entries) < 4 or (entries[3] != 'imię' and entries[3] != 'nazwisko'):
                continue
            spec = entries[2]
            if spec.startswith('subst:sg:gen:'):
                if entries[3] == 'imię':
                    first_name_dict[entries[0]] = entries[1]
                else:
                    last_name_dict[entries[0]] = entries[1]
                # print(f'{entries[1]} {entries[0]}')
    print('completed flex map build')
    return first_name_dict, last_name_dict


def attempt_last_flex_manually(main_name):
    if main_name.endswith('a'):
        return re.sub(r'a$', '', main_name)
    elif main_name.endswith('ki'):
        return re.sub(r'ki$', 'ka', main_name)
    elif main_name.endswith('gi'):
        return re.sub(r'ki$', 'ga', main_name)
    elif main_name.endswith('li'):
        return re.sub(r'ki$', 'la', main_name)
    elif main_name.endswith('wy'):
        return re.sub(r'wy$', 'wa', main_name)
    elif main_name.endswith('iego'):
        return re.sub(r'iego$', 'i', main_name)
    elif main_name.endswith('cego'):
        return re.sub(r'cego$', 'cy', main_name)
    elif main_name.endswith('iej'):
        return re.sub(r'iej$', 'a', main_name)
    else:
        print(f'MAN FAILED: {main_name}')
        return main_name


def fill_nominal_flexion(person_list):
    (fnames, lastnames) = build_names_flexion_maps()
    for person in person_list:
        mns = person.main_name.split('-')
        mns_nom = []
        for mn in mns:
            if mn in lastnames:
                mns_nom.append(lastnames[mn])
            else:
                flex_manually = attempt_last_flex_manually(mn)
                mns_nom.append(flex_manually)
            person.main_name_nom = '-'.join(mns_nom)

        sns = person.sec_name.split()
        sns_nom = []
        for sn in sns:
            if sn in fnames:
                sns_nom.append(fnames[sn])
            else:
                print(f'Unknown first name: {sn}')
                sns_nom.append(sn)
        person.sec_name_nom = " ".join(sns_nom)
    pass


def write_output_csv(grouped_person_list, output_file):
    # write header

    with open(output_file, "w") as output:
        writer = csv.writer(output)
        writer.writerow(['WikiId', 'First name', 'Last name','Street count'])
        rows = map(lambda pers: [pers.wiki_id(), pers.sec_name_nom, pers.main_name_nom, pers.occurences()],grouped_person_list)
        writer.writerows(rows)


def coalesce_abbreviated_persons(abbreviations, grouped_person_list):
    norm_by_name = defaultdict(list)
    for pers in grouped_person_list:
        norm_by_name[pers.main_name].append(pers)
    abbreviated_not_found_by_main_name = []
    abbreviated_not_found_by_abbreviation = []
    abbreviated_match_count = 0
    for abbreviated in abbreviations:
        if abbreviated.main_name not in norm_by_name:
            abbreviated_not_found_by_main_name.append(abbreviated)
            # print(f'Could not find abbreviated {abbreviated} in name list')
        else:
            for person in norm_by_name[abbreviated.main_name]:
                if person.matches_abbreviation(abbreviated.sec_name):
                    person.coalesce(abbreviated)
                    abbreviated_match_count += 1
                    break
            else:
                abbreviated_not_found_by_abbreviation.append(abbreviated)
                # print(f'Could not find abbreviated {abbreviated} in person list for name {abbreviated.main_name}')
    return abbreviated_not_found_by_abbreviation, abbreviated_not_found_by_main_name


def group_infos_by_name(normal_infos):
    grouped = {}
    for ifo in normal_infos:
        key = (ifo.main_name, ifo.sec_name)
        if key not in grouped:
            grouped[key] = ifo
        else:
            grouped[key].coalesce(ifo)
    print(f'Reduced to {len(grouped)} items in dic.')
    grouped_person_list = list(grouped.values())
    return grouped_person_list


def read_teryt_file(input_file):
    with open(input_file) as csv_file:
        csv_reader = csv.reader(csv_file, delimiter=';')
        line_no = 1
        raw_person_infos = []
        ignored_streets = 0
        created_persons = 0
        for row in csv_reader:
            if line_no > 1 and len(row) > 8:
                n1 = row[7]
                n2 = row[8]
                if len(normalize_name_extra(n2)) > 0:
                    raw_person_infos.append(StreetPersonInfo(n1, n2))
                    created_persons += 1
                else:
                    ignored_streets += 1
            line_no += 1
        processed_lines = line_no - 1
        print(
            f'Processed file {input_file}, read: {processed_lines} lines, added {len(raw_person_infos)} info entries.')
    return created_persons, ignored_streets, processed_lines, raw_person_infos



def main():
    input_file = "teryt.csv"
    print(f'Reading teryt csv file {input_file}...')

    created_persons, ignored_streets, processed_lines, raw_person_infos = read_teryt_file(input_file)

    normal_infos = list(filter(lambda i: not i.is_abbreviation(), raw_person_infos))
    abbreviations = list(filter(lambda i: i.is_abbreviation(), raw_person_infos))
    print(f'Got: {len(normal_infos)} regular entries and  {len(abbreviations)} abbreviated entries.')
    grouped_person_list = group_infos_by_name(normal_infos)

    abbreviated_not_found_by_abbreviation, abbreviated_not_found_by_main_name = coalesce_abbreviated_persons(
        abbreviations, grouped_person_list)

    grouped_person_list.sort(key=lambda i: i.occurences(), reverse=True)


    print(' == Execution report:')
    total_occurences = reduce(lambda x, y: x + y, list(map(lambda x: x.occurences(), grouped_person_list)))
    print(
        f''' Total lines: {processed_lines}
Found persons: {created_persons}
Ignored streets: {ignored_streets}
Successfully matched totally {total_occurences} into total persons: {len(grouped_person_list)}'''
    )

    print(f' ============== Missing abbreviated ==============')
    print(f'Abbreviated not found by MAIN NAME')
    for a in abbreviated_not_found_by_main_name:
        print(f'\t{a.main_name} - {a.sec_name} ({a.known_sec_names})')
    print(f'Abbreviated not found by INITIAL')
    for a in abbreviated_not_found_by_abbreviation:
        print(f'\t{a.main_name} - {a.sec_name} ({a.known_sec_names})')

    fill_nominal_flexion(grouped_person_list)
    print(f' \n\n============== Found people: ==============')
    for pers in grouped_person_list:
        print(f'{pers}')

    write_output_csv(grouped_person_list, "teryt_person_rank.csv")

    exit(0)



if __name__ == "__main__":
    main()

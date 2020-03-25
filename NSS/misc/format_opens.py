import glob
import xml.etree.ElementTree as ET
from collections import defaultdict
from itertools import dropwhile

def is_xer(x):
    def is_x(line):
        try:
            return line.split()[0] == x
        except:
            return False
    return is_x

is_module = is_xer('module')
is_open = is_xer('open')

def group_by(iterable, key=id):
    groups = defaultdict(set)
    for item in iterable:
        groups[key(item)].add(item)
    return groups

def format_opens(lines):
    pairs = [line.partition(line.split()[1])[1:] for line in lines]
    opens = []
    groups = group_by(pairs,key=lambda pair: get_rank(pair[0]))
    for rank in sorted(groups.keys()):
        opens.extend([f'open {name}{rest}' for name,rest in sorted(groups[rank])] + ['\n'])
    return opens

def get_parts(module):
    chunks = module.split('.')
    for i in range(len(chunks)):
        yield '.'.join(chunks[:i+1])

def get_groups():
    fsproj = ET.parse('NeutronDiffusion/NeutronDiffusion.fsproj')
    root = fsproj.getroot()
    groups = defaultdict(set)
    for group in root.findall('ItemGroup'):
        for source in group.iter():
            source_name = source.get('Include')
            if source_name:
                if source_name.endswith('.fs'):
                    source_name = source_name[:-3]
                for part in get_parts(source_name):
                    groups[source.tag].add(part)
    return groups

groups = get_groups()
group_rank = {'PackageReference': 1, 'Compile': 2}

def get_rank(module):
    parts = list(get_parts(module))
    for k,v in groups.items():
        if any(part in v for part in parts):
            return group_rank[k]
    return 0 # in standard libraries

for filename in glob.glob('NeutronDiffusion/*.fs'):
    module = ''
    opens = []
    rest = []
    with open(filename, 'r') as fp:
        for line in fp:
            if is_module(line):
                module = line + '\n'
            elif is_open(line):
                opens.append(line)
            else:
                rest.append(line)
    opens = format_opens(opens)
    rest = list(dropwhile(str.isspace, rest))
    with open(filename, 'w') as fp:
        fp.writelines([module] + opens + rest)

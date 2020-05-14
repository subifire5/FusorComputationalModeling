import os.path
import glob

from pyne.endf import Evaluation

def make_csv(xs):
    return '\n'.join([ f'{e},{cs}' for e, cs in zip(xs.x, xs.y) ]) + '\n'

def write_reaction_csv(directory, e, mat, mt):
    with open(os.path.join(directory, mat), 'w') as f:
        f.write(make_csv(e.reactions[mt].xs))

for filename in glob.glob('./pendf/*'):
# for filename in ['./pendf/125']:
    mat = os.path.basename(filename)
    try:
        e = Evaluation(filename)
        e.read()

        write_reaction_csv('./csv/total', e, mat, 1)
        write_reaction_csv('./csv/elastic', e, mat, 2)
    except ValueError:
        print(f'pendf failed to parse {filename}')

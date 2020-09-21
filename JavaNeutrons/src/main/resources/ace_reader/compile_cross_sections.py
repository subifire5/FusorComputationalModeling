import os.path
import glob

from pyne import ace
print glob.glob('../data/ace/6012.*')
libFile = ace.Library('../data/ace/6012.800nc.ace')
libFile.read()
print libFile.tables
table = libFile.tables['6012.800nc']
# print table.energy
# print table.sigma_a
# print table.sigma_t

print table.reactions
print table.reactions[102]
print vars(table.reactions[102]).keys()

x= 1/0
print table.reactions[2]
print vars(table.reactions[2]).keys()

start = 94
stop = 95
print ""
print "Energies"
print table.reactions[2].ang_energy_in[start:stop]
print ""
print "cosines"
print table.reactions[2].ang_cos.values()[start:stop]
print ""
print "pdf"
print table.reactions[2].ang_pdf.values()[start:stop]
print ""
print "cdf"
print table.reactions[2].ang_cdf.values()[start:stop]
exit 
x = 1/0


def make_csv(t):
    return '\n'.join([(str(e*1e6)+','+str(s)+','+str(a)+','+str(total)) for (e, s, a, total) \
        in zip(t.energy, t.reactions[2].sigma, t.sigma_a, t.sigma_t)]) + '\n'
def write_reaction_csv(directory, t, mat):
    with open(os.path.join(directory, mat+'.csv'), 'w') as f:
        f.write("energy (eV),scatter (barn),capture (barn),total (barn)\n")
        f.write(make_csv(t))

def make_angle_csv(t):
    return '\n'.join([(str(e*1e6)+','+str(count)+','+str(cdf).replace("\n"," ")+','+str(cos).replace("\n"," ")) for (e, count, cdf, cos) \
        in zip(t.reactions[2].ang_energy_in, 
        [len(c) for c in t.reactions[2].ang_cos.values()],
        t.reactions[2].ang_cdf.values(), 
        t.reactions[2].ang_cos.values())]) + '\n'

def write_angle_csv(directory, t, mat):
    with open(os.path.join(directory, mat+'_angle.csv'), 'w') as f:
        f.write("energy (eV),count,cdf,cos\n")
        f.write(make_angle_csv(t))

write_reaction_csv('.', table, '1001')
write_angle_csv('.', table, '1001')

for filename in glob.glob('../data/ace/*.ace'):
    mat = os.path.basename(filename)
    try:
        libFile = ace.Library(filename)
        libFile.read()
        values_view = libFile.tables.values()
        value_iterator = iter(values_view)
        table = next(value_iterator)

        write_reaction_csv('../data/ace', table, mat)
        write_angle_csv('../data/ace', table, mat)
    except ValueError:
        print('ac_reader failed to parse '+filename)

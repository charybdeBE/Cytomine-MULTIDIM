#!/bin/bash
#SBATCH -c 16
#SBATCH --mem=512G
export JAVA_OPTS="-Xms120G -Xmx512G"
groovy \
-cp .:/home/mass/GRD/laurent.vanosmael/code/Cytomine-MULTIDIM/lib/cisd-args4j.jar:/home/mass/GRD/laurent.vanosmael/code/Cytomine-MULTIDIM/lib/commons-io.jar:/home/mass/GRD/laurent.vanosmael/code/Cytomine-MULTIDIM/lib/commons-lang.jar:/home/mass/GRD/laurent.vanosmael/code/Cytomine-MULTIDIM/lib/sis-base.jar:/home/mass/GRD/laurent.vanosmael/code/Cytomine-MULTIDIM/lib/sis-jhdf5.jar:/home/mass/GRD/laurent.vanosmael/code/Cytomine-MULTIDIM/lib/sis-jhdf5-batteries_included.jar:/home/mass/GRD/laurent.vanosmael/code/Cytomine-MULTIDIM/lib/sis-jhdf5-core.jar:/home/mass/GRD/laurent.vanosmael/code/Cytomine-MULTIDIM/lib/sis-jhdf5-tools.jar:/home/mass/GRD/laurent.vanosmael/code/Cytomine-MULTIDIM/src/ \
/home/mass/GRD/laurent.vanosmael/code/Cytomine-MULTIDIM/src/be/charybde/multidim/hdf5/input/Main.groovy 

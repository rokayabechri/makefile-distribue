cd makefiles/matrix/;
java -jar ../../MakeDistribCoreServer.jar Makefile &
cd -;
python deploy.py 127.0.0.1 13337

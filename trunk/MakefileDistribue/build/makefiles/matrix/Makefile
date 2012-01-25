all: c

check:	a b multiply
	 ./multiply check a b
c:	c-1-1 c-1-2 c-2-1 c-2-2 fuse
	./fuse c 2 2 c-1-1 c-1-2 c-2-1 c-2-2

#
c-1-1:	p-1-1-1-1 p-1-2-2-1 sum
	./sum c-1-1 p-1-1-1-1 p-1-2-2-1
c-1-2:	p-1-1-1-2 p-1-2-2-2 sum
	./sum c-1-2 p-1-1-1-2 p-1-2-2-2
c-2-1:	p-2-1-1-1 p-2-2-2-1 sum
	./sum c-2-1 p-2-1-1-1 p-2-2-2-1
c-2-2:	p-2-1-1-2 p-2-2-2-2 sum
	./sum c-2-2 p-2-1-1-2 p-2-2-2-2

#
p-1-1-1-1:	a-1-1 b-1-1 multiply
	./multiply p-1-1-1-1 a-1-1 b-1-1
p-1-2-2-1:	a-1-2 b-2-1 multiply
	./multiply p-1-2-2-1 a-1-2 b-2-1
p-1-1-1-2:	a-1-1 b-1-2 multiply
	./multiply p-1-1-1-2 a-1-1 b-1-2
p-1-2-2-2:	a-1-2 b-2-2 multiply
	./multiply p-1-2-2-2 a-1-2 b-2-2
p-2-1-1-1:	a-2-1 b-1-1 multiply
	./multiply p-2-1-1-1 a-2-1 b-1-1
p-2-2-2-1:	a-2-2 b-2-1 multiply
	./multiply p-2-2-2-1 a-2-2 b-2-1
p-2-1-1-2:	a-2-1 b-1-2 multiply
	./multiply p-2-1-1-2 a-2-1 b-1-2
p-2-2-2-2:	a-2-2 b-2-2 multiply
	./multiply p-2-2-2-2 a-2-2 b-2-2

#
a-1-1:	a split
	./split a-1-1 a 2 2 1 1
a-1-2:	a split
	./split a-1-2 a 2 2 1 2
a-2-1:	a split
	./split a-2-1 a 2 2 2 1
a-2-2:	a split
	./split a-2-2 a 2 2 2 2

#
b-1-1:	b split
	./split b-1-1 b 2 2 1 1
b-1-2:	b split
	./split b-1-2 b 2 2 1 2
b-2-1:	b split
	./split b-2-1 b 2 2 2 1
b-2-2:	b split
	./split b-2-2 b 2 2 2 2
clean:
	rm -f a-1-1 a-1-2 a-2-1 a-2-2 b-1-1 b-1-2 b-2-1 b-2-2 p-1-1-1-1 p-1-2-2-1 p-1-1-1-2 p-1-2-2-2 p-2-1-1-1 p-2-2-2-1 p-2-1-1-2 p-2-2-2-2 c-1-1 c-1-2 c-2-1 c-2-2 c check

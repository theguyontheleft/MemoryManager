Memory Manager
========

This program implements a Bintree datastructure for storing watcher records where this Bintree is stored in a disk ﬁle. There is a series of commands to process information. The main operations are inserting watchers,
deleting watchers, locating all watchers within a certain distance of a search point, and outputting
a traversal of the Bintree for debugging purposes.
The Bintree and its Watcher records will reside on disk. A buﬀer pool (using the LRU replacement strategy)
will mediate access to the disk ﬁle, and a memory manager will decide where in the disk ﬁle to store
the Bintree nodes as well as the Watcher records. Another way to look at this is: there is a
memory manager, which is a way to store data in a big array. The program will store the Bintree
nodes and watcher records in the memory manager’s array. The memory manager’s array is really
a disk ﬁle, and will use a buﬀer pool to manage the disk I/O.


Input and Output:
========
The program will be invoked from the command-line as:
java DiskBintree "command-file-name" "numb-buffers" "buffersize"

The "command-file-name" parameter is the name of the command input ﬁle that provides a
series of commands to process. The "numb-buffers" parameter is the number of buﬀers in the
buﬀer pool, and will be in the range 1–20. Parameter "buffersize" is the size of a buﬀer in the
buﬀer pool (and therefore determines the amount of information read/written on each disk I/O
operation).
This program will create and maintain a disk ﬁle which stores the memory manager’s array. The
buﬀer pool acts as the intermediary for this ﬁle. The name of this disk ﬁle is “p4bin.dat”.
After completing all commands in the input ﬁle, all unwritten blocks in the buﬀer pool are
written to disk, and “p4bin.dat” is closed, not deleted. Three properties of the bufferPool:
First, the buﬀer pool's size for the buﬀers is determined by the command line parameter. Second, since the data entities stored on disk are of variable size (diﬀererent tree node types, and the Watcher records contain a variable-length string), the buﬀer pool can handle storing messages that span block boundaries, or that might
even span multiple blocks. Third, you need to be careful not to read in blocks from the ﬁle that
do not yet exist. This can happen when the memory manager decides to “grow” the size of its
memory pool (and therefore directs the buﬀer pool to write a message to a part of the ﬁle that
does not yet exist). ﬁle that does not yet
The command ﬁle will contain a series of commands (some with associated parameters, sepa-
rated by spaces), one command for each line. The commands will be read from the ﬁle given in
command line parameter 1, and the output from the command will be written to standard output.


"add x y name"
Adds a watcher record to the bintree. The X and Y coordinates are
longitude and latitude, respectively, and so range between (-180, 180) and (-90, 90), resprectively.
On successfully adding a record, this program will print
"name x y is added to the bintree"
If the record duplicates the X and Y coordinates of an existing record then the program will reject it
and print
"name x y duplicates a watcher already in the bintree"
Next is the delete command, which looks as follows.
"delete x y"
If successful, it prints
"name x y is removed from the bintree"
If a record with those coordiantes does not exist, then print
"There is no record at x y in the bintree"
The search command looks as follows.
"search x y radius"
First the program will print out the line
"Search x y returned the following watchers:"
Then it will print out each record that lies in the search circle listing its name and position,
one record to each line. After the program completes searching, it will print out
"Watcher search caused 'number' bintree nodes to be visited."
Finally, there is the debug command. It will print a traversal for the bintree nodes. After that, it will print the block IDs of the blocks currently
contained in the buﬀerpool in order from most recently to least recently used. Note that “Block
ID” simply refers to the block number, starting with 0. Thus, if the block size is 1024 bytes, then
bytes 0-1023 are in Block 0, bytes 1024-2047 are in Block 1, and so on.
After the program processes all of the commands in the watcher ﬁle, it will print out summary
statistics for the number of cache hits and misses and the number of disk reads and writes.

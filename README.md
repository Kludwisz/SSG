# SSG
Seedfinding for a new ssg seed<br>
Usage:<br>
java -jar ssg.jar -out "(full_path_to_output_file)" -threads (threadCount) -start (startTaskID) -end (endTaskID)<br>

Example:<br>
java -jar ssg.jar -out "C:\Users\Kludwisz\Documents\SSGoutput.txt" -threads 16 -start 0 -end 31<br>
would find candidate seeds in the range 0 - 32 * 10^10

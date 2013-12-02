gtf2bed
=======

Convert GTF files from, e.g., GENCODE, into a slightly-augmented BED format for use with
Dalliance.

Building
--------

Install Leiningen then:

      lein deps
      lein uberjar

Compiled jar files will end up in the `target` directory.  If you use the -standalone jar file,
there shouldn't be any other requirements to run it.

To make bigbed files for use with Dalliance, you'll probably want the JKSrc tools from UCSC, or
at least a recent version of `bedToBigBed`.

Converting genes
----------------

		java -jar target/gtf2bed-0.0.1-SNAPSHOT-standalone.jar gencode.gtf >gencode.bed

Making bigbed files for Dalliance
---------------------------------

		bedToBigBed -type=bed12+3 -as=gencode.as gencode.bed hg19.chromInfo gencode.bb

You'll probably also want to make a search-by-gene-name index using `ixIxx`.  Helper script
for that coming soon....
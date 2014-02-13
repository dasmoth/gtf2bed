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

		bedToBigBed -type=bed12+4 -as=gencode.as -extraIndex=name gencode.bed hg19.chromInfo gencode.bb

Making gene-name index
----------------------

                cut -f 4,14 gencode.bed >gencode.txt
                ixIxx gencode.txt gencode.ix gencode.ixx

Note that you need to keep both the .ix and .ixx files.

Configuring a source in Dalliance
----------------------------------

                      {name: 'Genes',
                       desc: 'Gene structures from GENCODE 19',
                       bwgURI: 'http://www.biodalliance.org/datasets/gencode19.bb',
                       stylesheet_uri: 'http://www.biodalliance.org/stylesheets/gencode.xml',
                       collapseSuperGroups: true,
                       trixURI: 'http://www.biodalliance.org/datasets/genecode19.ix'}

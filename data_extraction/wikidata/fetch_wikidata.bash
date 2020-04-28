#!/bin/bash
set -x
for sparql in *.sparql;
do
	echo "Processing file $sparql"
	query=$(cat $sparql)
	output=`basename $sparql .sparql`.csv
        curl -H "Accept: text/csv" -o "$output" -v --data-urlencode "query=$query" https://query.wikidata.org/sparql
	line_count=$(wc -l $output)
	echo "Completed, output $output has $line_count lines"
	sleep 60
done

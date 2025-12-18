#!/bin/bash

YEAR=2026   # change year if needed

for month in {01..12}; do
  for day in {01..31}; do
    DATE="$YEAR-$month-$day"

    # Skip invalid dates
    if date -j -f "%Y-%m-%d" "$DATE" >/dev/null 2>&1; then
      echo "$DATE" >> commits.txt
      git add commits.txt
      GIT_AUTHOR_DATE="$DATE 12:00:00" \
      GIT_COMMITTER_DATE="$DATE 12:00:00" \
      git commit -m "Commit for $DATE"
    fi
  done
done

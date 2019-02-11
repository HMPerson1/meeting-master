#!/bin/bash
git --no-pager log --reverse --date=short --format="tformat:%ad  XXX hr  %h  %s" --since="$(date -d"last sunday" -I)" --author="$(git config user.name)"

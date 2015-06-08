#!/bin/sh
rm -rf /tmp/allProjects
mkdir /tmp/allProjects
cd /tmp/AllProjects
curl -s https://api.github.com/orgs/droolsjbpm/repos | ruby -rjson -e 'JSON.load(STDIN.read).each {|repo| %x[git clone #{repo["ssh_url"]} ]}'
curl -s https://api.github.com/orgs/dashbuilder/repos | ruby -rjson -e 'JSON.load(STDIN.read).each {|repo| %x[git clone #{repo["ssh_url"]} ]}'
curl -s https://api.github.com/orgs/uberfire/repos | ruby -rjson -e 'JSON.load(STDIN.read).each {|repo| %x[git clone #{repo["ssh_url"]} ]}'
cd -
 
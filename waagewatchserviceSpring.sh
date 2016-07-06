#!/bin/sh
#------- ABSCHNITT : Modul Identifikation -------------------------------------
#
# Modul Name       : Waagenwatchservice.sh
# .     Verwendung : Waagen Schnittstelle
# Autor            : TK
# Verantwortlich   : TK
# Beratungspflicht : nein
# Copyright        : abas GmbH
#
#------- ABSCHNITT : Modul Beschreibung ---------------------------------------
#
#------------------------------------------------------------------------------

#------- ABSCHNITT : Defines, Prozeduren, Typen und Daten ---------------------
DEVNULL=${DEVNULL:-/dev/null}; export DEVNULL
name=`basename $0`
whoami=`id -un | sed 's/^.*\\\\//'`  # wg. Windows: DOMAIN\user -> user
pwd=`pwd`

myexit()
{
  exitcode=$1
  errmsg="$2"
  if [ -n "$errmsg" ];then
      echo $name: "$errmsg" >&2
  fi
  exit $1
}

#------------------------------------------------------------------------------
# zeigt die Usage an
#------------------------------------------------------------------------------
usage() {
  echo  "" >&2
  
  echo "   * Option -stop: stop process to given configurationfile" >&2
  echo "" >&2
  exit 1
}

#------------------------------------------------------------------------------
# Pruefung auf Mandantenverzeichnis
#------------------------------------------------------------------------------
#mandtest.sh -f || exit 1
#waage_mandantdir=`pwd`

#------- ABSCHNITT : Optionen -------------------------------------------------
# Options- und Nichtoptionsargumente koennen Blanks enthalten.

configfile=
stopping=false
debug=false

waage_logging_props=/home/waage/java/log/config/logging.properties

while [ $# -gt 0 ]
do
  case "$1" in
   -start)  stopping=false
          shift
          ;;
   -stop) stopping=true
          ;;
   -debug) debug=true
         ;;
    -*) usage
        ;;
     *) break
        ;;
  esac
  shift
done

#[ -z "$configfile" ] && usage
#[ -f $configfile ] || myexit 1 "configuration file not found: $configfile"
#[ -f $waage_logging_props ] || myexit "logging.properties not found: $waage_logging_props"

#------- ABSCHNITT : Hauptprogramm --------------------------------------------
# main
debugString= 
if [ $debug == true ]; then
   debugString='-Xdebug -Xrunjdwp:transport=dt_socket,address=8017,server=y,suspend=y'	
fi


if [ $stopping == true ]; then
  wget http://localhost:9080/shutdown/ 
else
#  $JAVAPATH/bin/java -Dlog4j.configuration=file:${waage_logging_props} ${debugString}  -jar /home/waage/libs/waage-0.0.1.jar &
  $JAVAPATH/bin/java  ${debugString}  -jar /home/waage/libs/waage-0.0.1-SNAPSHOT.jar &
fi

exit 0

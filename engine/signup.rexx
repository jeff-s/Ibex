#!rexx

parse pull postdata

if postdata="" then signal oops

call procpost

thishost=getenv("HTTP_HOST")

if pos("hexibex.us",thishost)>0 then
  filedatadir="/kunden/homepages/20/d100701421/htdocs/hexibex/plugins/filedata/"
else
  filedatadir="/Users/jeff/Sites/ibex/filedata/"

race.0=4
race.1="Eye Guys"
race.2="Ophidians"
race.3="Teal Magi"
race.4="Purple Dragons"

say "Content-Type: text/html"
say

if length(post.dynasty)=0 | length(post.firstname)=0 then signal oops

say "<html>"
say "<head>"
say "<META HTTP-EQUIV=""refresh"" content=""3; URL=config.rexx/"||post.race||"/ibexconfig"">"
say "<title>Ibex Signup</title>"
say "<style>"
say "body {font-family:Verdana;background-color:black;color:white;}"
say ".ib {font-family:Verdana;font-size:9pt; font-weight:bold; background-color:#403040; color:#e0a0d0; border:solid 1px white;}"
say "</style>"
say "</head>"

say "<body>"
say "<h1>Ibex Signup</h1>"
therace=post.race

f="./players/"therace".player"
x=lineout(f,"[IbexPlayer]")
x=lineout(f,"player="therace)
x=lineout(f,"dynasty="post.dynasty)
x=lineout(f,"firstname="post.firstname)
x=lineout(f,"title="post.title)

/* --- */
f=filedatadir||"signups"
f2=f||"2"
x=lineout(f,"<b><a class=taxlink href=engine/profile.rexx/"||therace||">"||post.title post.firstname post.dynasty||"</a></b> of the" race.therace||".<br/>")
x=lineout(f2,"<p><b><a class=taxlink href=engine/profile.rexx/"||therace||">"||post.title post.firstname post.dynasty||"</a></b> of the" race.therace||".</p>")

say "<p>Your game file is being prepared," post.title post.firstname post.dynasty "of the" race.therace".</p>"
say "<p>Shortly, you will be prompted to download a file.  Save this in your Ibex directory.</p>"

say "</body></html>"

call logg
exit

/*------------------------------*/
logg:
  parse arg m

  parse source . . me
  me=substr(me,lastpos("/",me)+1)

  thishost=getenv("HTTP_HOST")
  
  if pos("hexibex.us",thishost)>0 then
    logfile="/kunden/homepages/20/d100701421/htdocs/hexibex/engine/gw.log"
  else
    logfile="/Users/jeff/Sites/ibex/gw.log"
  
  call lineout logfile,me date("U") time() getenv("REMOTE_ADDR") therace """"||post.dynasty||"""" m """"||getenv("HTTP_USER_AGENT")||""""
return 0


procpost:
flag=0
rest=postdata
do while length(rest)>0
  parse var rest +0 a "=" b "&" rest
  upper a
  b=deweb(b,"+")
  post.a=b
end
return


DeWeb: PROCEDURE; PARSE ARG Input, Opts/* *******************************************DeWeb converts hex encoded (e.g. %20=space) characters in the Input string to the equivalent ASCII characters and returns the decoded string.If the 2 characters following a % sign do notrepresent a hexadecimal 2 digit number, then the % and following 2 characters are returnedunchanged. If the string terminates with a % thenthe % sign is returned unchanged. If the finaltwo characters in the string are a % sign followed by a single hexadecimal digit then  they are returned unchanged.The optional Options argument contains a set of characters which allows you to tell DeWeb to:'+' convert plus signs (+) to psaces    in the input before the hex decoding is done.'*' convert asterisks (*) to percent signs (%)     after the decoding.  This option    is often used with Oracle.Examples:  SAY DeWeb('%3Cpre%3e%20%%25Loss  %Util%')   results in:  ' %%Loss  %Util%'  SAY DeWeb('%3cpre%3eName++Address%','*+')  results in   'Name  Address*'******************************************* */IF POS('+',Opts)>0 THEN Input=TRANSLATE(Input,' ','+')Start=1; Decoded=''DO WHILE POS('%',SUBSTR(Input,Start))^=0   String=SUBSTR(Input,Start)   PARSE VAR String Pre'%'+1 Ch +2 Input   IF DATATYPE(Ch,'X') & LENGTH(Ch)=2 THEN         Ch=X2C(Ch)   ELSE DO; Input=Ch||Input; Ch='%'; END   Start=LENGTH(Decoded||Pre||Ch)+1   Input=Decoded||Pre||Ch||Input   Decoded=Decoded||Pre||ChENDIF POS('*',Opts)>0 THEN Input=TRANSLATE(Input,'%','*')RETURN Input


oops:
say "<p style=""color:red""> invalid info. </p>"

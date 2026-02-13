grammar CoSyFeAT;

fmodel : xmltag featuremodel+ ;
xmltag : '<?xml' tag '?>';
featuremodel : '<featureMC>' (typemodel | groupmodel)+ (constraint)* '</featureMC>';

typemodel : '<featuremodel type="type"' 'id="'modelid'"' '>' model '</featuremodel>';
groupmodel : '<featuremodel type="group"' 'id="'modelid'"' '>' gmodel '</featuremodel>';

modelid :  NUMBER ;
instances :  NUMBER;

model : contexttree systemtree;
contexttree : '<context>' ftree '</context>';
systemtree : '<system instances="' instances '">'  ftree '</system>';
gmodel : ftree;

ftree : and | or | alt | feature;
and : '<and name="' fname '"' relation* '>' (and|or|alt|feature)* '</and>';
or : '<or name="' fname '"' relation* '>' (and|or|alt|feature)* '</or>' ;
alt : '<alt name="' fname '"' relation* '>' (and|or|alt|feature)* '</alt>' ;
feature : '<feature name="' fname '"'  relation* ('/>'|'>' '</feature>') ;
relation : 'relation="' relationname '"';
relationname : NAME;
instnumber : NUMBER;
fname : (NAME|NUMBER)+;

constraint : '<constraint>'(requires | excludes)+ '</constraint>';
requires : '<requires sourceid="' sourceid '"' 'sourcetype="' sourcetype '"' 'source="' source '"' 'targetid="' targetid '"' 'targettype="' targettype '"' 'target="' target '"' ('cardinality="' cardinality '"')* '/>';
excludes : '<excludes sourceid="' sourceid '"' 'sourcetype="' sourcetype '"' 'source="' source '"' 'targetid="' targetid '"' 'targettype="' targettype '"' 'target="' target '"/>';
source : (NAME|NUMBER|SYMBOL)+;
target : (NAME|NUMBER|SYMBOL)+;
sourcetype : NAME;
targettype : NAME;
sourceid : NUMBER;
targetid : NUMBER;
cardinality : lowerbound '..' upperbound;

upperbound : NUMBER|'*';
lowerbound : NUMBER|'*';

WS : [ \t\r\n]+ -> skip;
NAME :( [a-zA-Z]+ | '-' | '_')+ ;
NUMBER : [0-9]+ ;
tag : (NAME| '<' | '>' | '=' | '-' | '?'| '"' | '.' | NUMBER)+ ;


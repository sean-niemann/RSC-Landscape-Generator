# RSC-Landscape-Generator
Command line tool to generate a visual representation for every format of the RS Classic (RSC) landscape files.

##Loading
Drag landscape files into the appropriate directory for its type

For RSCD, [*input/rscd/Landscape.rscd*]

For Jag (maps28 and over), [*input/jag/maps.jag; input/jag/land.jag; input/jag/maps.mem; input/jag/land.mem*]

For Jag Legacy (maps27 and under), [*input/jag-legacy/maps.jag*]

##Running
java -jar mapgen.jar [rscd|jag|jag-legacy]

*Note: I've preloaded some maps for each type in the zip file already*

###It's pretty interesting to see how the development of the rsc world progressed
map14  (Creation of major cities and player owned houses) - [PICTURE](http://i.imgur.com/ajV79i1.png "map14")

map19 (Creation of karamja volcano) - [PICTURE](http://i.imgur.com/6VdrKVY.png "map19")

map22 (Removal of player owned houses in varrock and falador) - [PICTURE](http://i.imgur.com/POWUYST.png "map22")

map27 (Creation of the wilderness and crandor isle) - [PICTURE](http://i.imgur.com/AIIFIlG.png "map27")

Here's an ingame screenshot of map14 at varrock POH's - [PICTURE](http://i.imgur.com/9Gj0Icz.png "map14 (ingame)")

*I've supplied a legacy map pack (.zip) if you're curious to test them out yourself*

###Here's an example for what this generator will produce for the RSCD file format:
![RSCD Example](http://i.imgur.com/qPLZVpD.png "RSCD Example")

Here's a legacy map pack (.zip) if you're curious to test them out yourself - [LINK](https://www.dropbox.com/s/4q4ue36xp8qxoaf/legacy-maps.zip?dl=0 "Legacy Map Pack")

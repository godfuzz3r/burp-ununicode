## UnUnicode


A burpsuite extention mostly based on this repository: asd https://github.com/bit4woo/u2c

## Why

Plugin from repository above didn't work in my burp suite, so I decided to make a simple replacement that uses StringEscapeUtils.unescapeJava to convert unicode escape sequences to single-byte characters.

## Example

Below is an example of how a json with a unicode escape sequence can be viewed in a burp without converting it in python cli or smth.
You may notice that the double-byte unicode character (emoji at the end of the json) was not converted correctly, but this is enough for my routine tasks.

![default pretty print](img/1.png)

![decode unicode escape sequences](img/2.png)

This is works on any content type as well, but pretty print implemented to json only:

![simple text](img/3.png)

## Install

```bash
mvn clean install
# install target/ununicode-1.0-jar-with-dependencies.jar
# in your burp extentions
```

Much thanks to [bit4woo](https://github.com/bit4woo) and [u2u](https://github.com/bit4woo/u2c) project for code base.
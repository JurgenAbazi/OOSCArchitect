# OOSC Architect

### Group 13 (Philipp Hertweck, Tobias Kiel, Jurgen Abazi)

An interior design planner software using the JHotDraw7 Java framework. Part of the 5th Assignment for the 
Object-Oriented Software Construction course at RWTH Aachen. 

## Known issues

### Missing resources

When the application is launched, it will complain in the logs about some missing resources, which can be ignored.

```
Warning ResourceBundleUtil[org.jhotdraw.draw.Labels].getIconProperty "view.increaseHandleDetailLevel.smallIcon" not found.
```

### CRC corruption error

When the application is launched, it will show the following error in the logs:

```
sun.awt.image.PNGImageDecoder$PNGException: crc corruption
```

Reason for this has not been investigated, but everything seems to work fine anyway.
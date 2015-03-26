## Introduction

ACIT stands for ACtive/ACtor ITem

Right now it provides binding for (only) RollerShutter items …

This binding creates RollerShutters items above another RollerShutter item (the so called itembehind item)

It does create a "virtual" item on top of another physical or virtual item

The mapping provides two main features:
- re-calibration of the 0-100 range using a mapping function
- resynch to achieve a more accurate positioning of the shutter

I wrote this bundle mainly for fun, but also because my shutters are not so accurate in positioning:
1. sending go to e.g. 95 at two different shutters yields to very different positioning
1. if you move up and down often you end-up with a completely out of sync shutter


Any command sent to an "active" item is translated to [at least] a command on the item behind referred in its binding section.

Mapping works on both direction: any command/update to/from the item behind will create an update on the active item

When an accurate position is required or the shutter is out of sync the active item will resync the item below going to full-open or full-closed position before moving to the desired position

## Items definition example

Assuming in your item definition file you have a Rolleshutter item (e.g. one mapped on knx) like the one below
Rollershutter Shutter_IN_Office                 "Studio [%d %%]"        (IN_Office, Shutters)           { knx="2/0/0, 2/1/0, 5.004:2/3/0+<2/2/0" }

You can define an "active" item like the following

Rollershutter A_Shutter_IN_Office               "Studio A[%d %%]"     (IN_Office, Shutters)             { acit="Shutter_IN_Office;0:0;90:82.3;98:98;100:100" }

Any command sent to A_Shutter_IN_Office item is translated to [at least] a command on the item behind referred in its binding section.
In the above example goto 90% command sent to A_Shutter_IN_Office is translated to go 82.3 on the Shutter_IN_Office item
In any other point beetween the specified mapping a linear interpolation is used.




## Disclaimer

Most likely I violated several openhab basic principles for bindings

Just to have a bit of fun the bundle is coded using scala language and akka framework ("slightly" overkilling)

Please forgive me if my code does not sound so functional...it's my scala hello world ;-)


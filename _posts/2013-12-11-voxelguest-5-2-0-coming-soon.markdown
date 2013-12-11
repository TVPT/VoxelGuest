---
layout: post
title:  "VoxelGuest 5.2.0 Coming Soon"
date:   2013-12-11 00:00:38
categories: update
---

[VoxelGuest][voxelguest] is one of the core plugins on [The VoxelBox][voxelbox] - and it is about to get some awesome updates!

What's new in 5.2.0?
---
The region module has been rewritten completely. This is probably one of the biggest changes in the new version.

Supporting **modular rule-based region protection**, [VoxelGuest's][voxelguest] *Regionator* can now be extended by other plugins! This unique feature allows other
plugin developers to hook into our API and simply add their own sets of *rules*. A rule can be anything - from a basic mob spawn regulation component to a complex
build and chat permission system.

**Region snapshotting** - another extremely helpful feature of [VoxelGuest][voxelguest] is what's called region snapshotting. You might know the term **snapshotting** from virtualization
solutions like ESXi or VirtualBox. It basically allows you to save the state of a virtual machine so you can roll back to this state later.

In terms of VoxelGuest, you can think of this as some kind of live backup. It reduces the downtime of your server and also minimizes the size of backups. There is no need to copy your whole world
if all you care about is a small subset of if.

GZIP compression and a delta algorithm reduce the size of backups even more.

**Smarter command processing!** Ok well, end users won't notice too much. However, the command processing code is much cleaner thanks to [args4j], a Java library for command
option and argument parsing.


Release date, please?
---
There will be two beta/testing releases in December. - **Beta1** has been release on December 7 ([Build #134][build-beta1]) and **Beta2** for December 14. If both beta phases pass without any bigger issues/show stoppers, the **final stable** release will be on December 25!

[voxelguest]: /projects/voxelguest.html
[voxelbox]: http://thevoxelbox.com
[voxelwiki]: http://voxelwiki.com
[args4j]: http://args4j.kohsuke.org
[build-beta1]: http://ci.thevoxelbox.com/job/VoxelGuest/134/


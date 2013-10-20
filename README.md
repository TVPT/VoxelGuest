VoxelGuest
==========

The user management and administrative plugin from The Voxel Box. More information on the [VoxelWiki][VoxelWiki].

Compilation
-----------

We use maven to handle our dependencies.

- Install [Maven 3][Maven]
- Check out this repository.
- Run ```mvn clean package```

The master branch is automatically build on our Jenkins CI server ([VoxelGuest Jenkins Job][JenkinsJob]).

Issue Tracker Notes
-------------------

How do I create a ticket the right way?

- Separate your reports. You think there is something wrong, but also want this new feature? Make life easier for us and create two tickets. We'd appreciate it big times.
- Don't tell us your story of life. We want facts and information. The more information about `the Problem` you give us, the easier it is for us to figure out what's wrong.
- Check the closed tickets first. Maybe someone created a similar ticket already. If you think it's unresolved, then give us more information on there instead.

### Bug Report

Make sure to not tell us your story of life. We want brief descriptions of what's wrong to get directly to fixing.
Also try to make the title describe briefly what's wrong.

Include what you've expected to happen and what happened.

Also try and include information like version of VoxelGuest and Bukkit.

Additional Information like what Java version the server runs on would be appreciated, but is not required at all times.

### Enhancement Request

This is where imagination comes in. As mentioned, we don't want your story of life. We want to know what you think would be a good enhancement.

Try and include what you would enhance and how, how it would be useful and maybe why you made this proposal.

Keep in mind that those are guidelines.


We will still look into stuff that does not follow these guidelines, but it will give you an idea of what we want in a ticket and I would make our lives easier.

Pull Requests
-------------

We do accept pull requests and enhancements from third parties. Please try following the guidelines on how to submit your pull requests properly and how to format your code.

- Keep the number of commits to a minimum. We want to look over the commit and basically see what you've done.
- Try to comply to our coding guidelines (see below).
- Give us a good description to what you've done.
- Try to submit one change in one pull request and try to link it to the issue in the tracker if possible.

Coding Guidelines
-------------

- Code style should comply to our CheckStyle rules (checkstyle.xml). However, do not fix Checkstyle errors blindly. - Make smart decisions.
- Do. Not. Reference. CraftBukkit. Referencing a class in the CraftBukkit namespace will drastically decrease your pull request's chance of being accepted to 0.
- Comment your code. This includes JavaDocs and normal comments to clarify what your code is doing.
- Write tests. Less than 50% code coverage will result in declining your PR.
- Keep your code consistent. - Look at existing code and don't use 4 different ways to iterate over a list.


[VoxelWiki]: http://voxelwiki.com/minecraft/VoxelGuest
[JenkinsJob]: http://ci.thevoxelbox.com/job/VoxelGuest/
[Bukkit]: http://bukkit.org/
[Maven]: http://maven.apache.org/

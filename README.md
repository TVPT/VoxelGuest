VoxelGuest
==========

The user management and administrative plugin from The Voxel Box. More information on the [VoxelWiki][VoxelWiki].

Compilation
-----------

We use maven to handle our dependencies.

- Install [Maven 3][Maven]
- Check out this repository.
- Run ```mvn clean package```

The master branch is automatically build on our jenkins server ([VoxelGuest Jenkins Job][JenkinsJob]).

Issue Tracker Notes
-------------------

How do I create a ticket the right way?

- Separate your reports. You think there is something wrong, but also want this new brush? Make life easier for us and create two tickets. We'd appreciate it big times.
- Don't tell us your story of life. We want facts and information. The more information about `the Problem` you give us, the easier it is for us to figure out what's wrong.
- Check the closed tickets first. Maybe someone created a similar ticket already. If you think it's unresolved, then give us more information on there instead.

### Bug Report

Make sure to not tell us your story of life. We want brief descriptions of what's wrong to get directly to fixing.
Also try to make the title describe briefly what's wrong.

Include what you've expected to happen and what happened.

Also try and include information like version of VoxelGuest and Bukkit.

Additional Information like what java version the server runs on would be appreciated, but is not required at all times.

### Enhancement Request

This is where imagination comes in, but make sure to keep as it easy for us. As mentioned, we don't want your story of life. We want to know what you think would be a good enhancement.

Try and include what you would enhance and how, how it would be useful and maybe why you made this proposal.

Keep in mind that those are guidelines.
We will still look into stuff that does not follow these guidelines, but it will give you an idea what we want in a ticket and make our life easier.

Pull Requests
-------------

We do accept pull requests and enhancements from third parties. Guidelines how to submit your pull requests properly and how to format your code will come.

Some rough guidelines for now:

- Keep the number of commits to a minimum. We want to look over the commit and basically see what you've done.
- Coding guidelines should try to comply to the checkstyle rules (checkstyle.xml) but not blindly. Use your mind to make smart decisions.
- Give us a good description to what you've done.
- Try to submit one change in one pull request and try to link it to the issue in the tracker if possible.

[VoxelWiki]: http://voxelwiki.com/minecraft/VoxelGuest
[JenkinsJob]: http://ci.thevoxelbox.com/job/VoxelGuest/
[Bukkit]: http://bukkit.org/
[Maven]: http://maven.apache.org/

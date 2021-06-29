# Deploying new libpylons releases to the Maven Central Repository

## Requirements
- Your PGP public key needs to be uploaded to a keyserver. [peegeepee](https://peegeepee.com/) is fine.
- You must have been added to our Sonatype OSSRH reporitory. (We don't have a system in place for doing this atm; hit afti up about this and she'll do it.)
- Your keyring needs to be exported as _secring.kbx_ and placed in the pylons_wallet/libpylons folder in your working copy. (.gitignore will prevent you from adding this to the repository.) This is necessary for the Nexus publish plugin atm; there may be a better way to do this. (If the path to your keyring is specified in your .gradle/gradle.properties, that will always be appended to your current working directory - hence the need for this workaround. Note that this behavior may be less insane on systems other than Windows! If your dev environment behaves in a non-insane way, please put your keys somewhere not-insane instead.)
- Your .gradle/gradle.properties file must contain the following lines:
```
signing.keyId=[YOUR_KEY_ID]
signing.secretKeyRingFile=secring.kbx
signing.password=[YOUR_PASSWORD]
```

## Deployment
Open a terminal window in the pylons_wallet working directory (if you're using IDEA, just use the terminal tab) and type:
```
gradlew :libpylons publishToSonatype closeSonatypeStagingRepository
```
If the operation is successful, log into OSSRH and make sure the staging repository looks good. As long as it does, you can release it.


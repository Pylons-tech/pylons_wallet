###Usage example

creating a cookbook on a local node: 

`gradlew txutil:createCookbook -Pfilepath=cb.json -Paddr=127.0.0.1:1317`

creating a recipe on a local node

`gradlew txutil:createRecipe -Pfilepath=recipe.json -Paddr=127.0.0.1:1317`

cookbooks are stored as JSON files in `(project)/cookbook`; recipes are stored in `(project)/recipe`

keys will be generated automatically and stored in `dev-keys` in your project root in lieu of any
proper keyring integration rn. you can dump a privkey you already have and want to use in there
as a raw hex string, if you want.

at present this is mostly a toy that demonstrates that the pylons wallet/gradle integration
works, in some sense. the ux is extremely not-there-yet and it's not 100% ready for primetime.
expect to encounter Problems. but it does let you build and submit the create-cookbook/create-recipe
messages out of gradle.

you will need to clone pylons_wallet and publish recipetool to your own _local_ maeven repository
in order to use it right now. the maven-publish plugin can do this.

`gradlew recipetool:publishToMavenLocal`

once that's done, you can add `id("tech.pylons.build.recipetool") version "0.1"`to your build.gradle,kts plugins block,
and `mavenLocal()` to your repositories block, and you should grab the plugin.
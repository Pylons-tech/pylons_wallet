#Pylons SDK RecipeTool

Simple automated recipe creation and management tools, provided as a Gradle plugin.

##Usage

Add `id("tech.pylons.build.recipetool") version "0.1"` to your project's plugins block.
If you're already set up w/ the Pylons SDK, you don't need to do anything else; you'll import the plugin and be able to
use it immediately.

You'll need to store your cookbooks/recipes as MetaCookbook/MetaRecipe objects under the appropriate directories in your
project's root folder - see the `samples` directory included in this project for a quick example of what those look like,
and consult the MetaCookbook.kt/MetaRecipe.kt files for reference. At present, you still need to hand-write those JSON
files.

If you have a keypair you want to use, store the secret key as a hex string in a file called `dev-keys.`

Copy `recipetool.json` from the `samples` folder to the root of your project directory,
and change the profile name to suit your preference.

Once you've done all this, you can edit the local copies of your recipes at your leisure and then sync them with the chain
by running the `smartUpdateCookbook` and `smartUpdateRecipe` Gradle commands.
package com.example.redditbrowser

import android.net.Uri


class RedditFetcher {

    fun ParsePost(): PostInfo {
        return PostInfo("", PostType.URL)
    }

    fun fetchPosts() {
        // TODO
        val out = ArrayList<PostInfo>()
        out.add(
            PostInfo(
                "You deserve a long life!",
                PostType.IMAGE,
                new_content_url = Uri.parse("https://i.redd.it/4qjhqliqh3331.jpg")
            )
        )
        out.add(
            PostInfo(
                "The economics of a typical cup of coffee [OC]",
                PostType.IMAGE,
                new_content_url = Uri.parse("https://i.redd.it/yh6fvavx53331.jpg")
            )
        )
        out.add(
            PostInfo(
                "Best friend is a vet tech and sent me this pic of her patient tonight. He’s watching a kangaroo documentary",
                PostType.IMAGE,
                new_content_url = Uri.parse("https://i.redd.it/4pudtzbj83331.jpg")
            )
        )
        out.add(
            PostInfo(
                "The precise moment lightning strikes the water",
                PostType.IMAGE,
                new_content_url = Uri.parse("https://i.imgur.com/4IjIdcD.jpg")
            )
        )
        out.add(
            PostInfo(
                "You got a friend in me",
                PostType.IMAGE,
                new_content_url = Uri.parse("https://i.redd.it/i4mvlg3i42331.jpg")
            )
        )
        out.add(
            PostInfo(
                "A guy goes to Las Vegas to gamble and he loses all his money. He doesn't even have enough for a cab, but he flagged one down anyway. He explained to the driver that he would pay him back next time and gave him his phone number, but the driver told him, \"Get the fuck out of my cab.\"",
                PostType.TEXT,
                new_body = "He walked all the way to the airport and got home. \n" +
                        "\n" +
                        "Some times rolls by and he decides to go back to Vegas again and this time he wins BIG.\n" +
                        "\n" +
                        " He gets his bags and is ready for the airport with all his new winnings.\n" +
                        "\n" +
                        " There are a line of cabs and at the very end he sees the driver from last time that kicked him out. \n" +
                        "\n" +
                        "He stood for a moment thinking how can he get his revenge on that driver.\n" +
                        "\n" +
                        " So, he gets in the first cab. \n" +
                        "\n" +
                        "\"How much is it to the airport?\" he asks. \n" +
                        "\n" +
                        "The driver says, \"\$15.\" \n" +
                        "\n" +
                        "\"Great, how much is it for a blowjob on the way there?\" \n" +
                        "\n" +
                        "The cab driver says, \"Get the fuck out of my cab.\" \n" +
                        "\n" +
                        "So he goes to the next one and asks the same thing.\n" +
                        "\n" +
                        " \"How much to airport?\" \n" +
                        "\n" +
                        "\"\$15.\" \n" +
                        "\n" +
                        "\"Great, how much for a blowjob on the way there?\" \n" +
                        "\n" +
                        "And that cab driver also tells him to get the fuck out of his cab.\n" +
                        "\n" +
                        " He does this all the way down the line of drivers, each one kicking him out.\n" +
                        "\n" +
                        " He finally gets to the last driver, the one from his last trip.\n" +
                        "\n" +
                        " He asks, \"Hey how much to the airport?\"\n" +
                        "\n" +
                        " Driver responds, \"\$15.\" \n" +
                        "\n" +
                        "\n" +
                        "The guy hands him \$15 and says, \"Great let's go!\" \n" +
                        "\n" +
                        "And so the driver leaves, slowly passing all the other drivers who are staring out their window while the guy in the back smiles back with a thumbs up."
            )
        )
        out.add(
            PostInfo(
                "Gutter ball",
                PostType.VIDEO,
                new_content_url = Uri.parse("https://i.imgur.com/l3Fku3d.gifv".removeSuffix("gifv") + "mp4")
            )
        )
        out.add(
            PostInfo(
                "NASA is opening the space station to \$35,000-a-night visits. A tourist who paid Russia \$30 million to get there a decade ago says it's a 'seismic shift.'",
                PostType.URL,
                new_content_url = Uri.parse("https://www.businessinsider.com/nasa-international-space-station-commercialization-tourists-2019-6")
            )
        )
        out.add(
            PostInfo(
                "car",
                PostType.VIDEO,
                new_content_url = Uri.parse("https://www.demonuts.com/Demonuts/smallvideo.mp4")
            )
        )
        out.add(
            PostInfo(
                "Time-lapse of a pilot descending into LAX on a cloudy night",
                PostType.VIDEO,
                new_content_url = Uri.parse("https://giant.gfycat.com/CloseCrazyFowl.mp4")
            )
        )  // from https://gfycat.com/closecrazyfowl
    }
}

package com.medina.juanantonio.domain

object ContractDataTypes {
    val activityTypes = listOf("Swim", "Bike", "Run", "Walk")

    val defaultPriceOptions = listOf(
        Pair("₱10", 10),
        Pair("₱20", 20),
        Pair("₱50", 50),
        Pair("₱100", 100)
    )

    val defaultContractExpiration = listOf(
        Pair("7 Days", 7),
        Pair("14 Days", 14),
        Pair("30 Days", 30),
        Pair("60 Days", 60),
        Pair("No Limit", -1)
    )

    val contractCompletionMessages = listOf(
        Triple(
            "Contract Completed 🎉",
            "You’ve completed this contract. Every kilometer brought you here — effort turned into progress.",
            "Thank you!"
        ),

        Triple(
            "Quest Finished 🏁",
            "Another quest complete. You showed up, stayed consistent, and finished strong.",
            "Nice!"
        ),

        Triple(
            "Challenge Cleared 💰",
            "Challenge cleared. Your activities added up and the goal has been fully achieved.",
            "Awesome!"
        ),

        Triple(
            "Goal Unlocked 🔓",
            "You’ve unlocked this goal through consistent effort. Every step counted.",
            "Got it!"
        ),

        Triple(
            "Milestone Reached 🚀",
            "Milestone reached. This wasn’t luck — it was consistency over time.",
            "Continue"
        ),

        Triple(
            "Well Done 🏃",
            "Well done. You followed through and completed the contract.",
            "Keep going"
        )
    )

    val withdrawConfirmationMessages = listOf(
        Triple(
            "Withdraw Contract? 😟",
            "This challenge isn't finished yet. You still have {{ withdrawLimitRemaining }} withdrawal(s) left. Are you sure you want to walk away before reaching the finish line?",
            "Withdraw Anyway"
        ),

        Triple(
            "Giving Up Already? 🏳️",
            "Your goal is still waiting for you. You only have {{ withdrawLimitRemaining }} withdrawal(s) remaining. A few more activities could make all the difference.",
            "Yes, Withdraw"
        ),

        Triple(
            "The Finish Line Is Disappointed 😔",
            "The contract was counting on you. You have {{ withdrawLimitRemaining }} withdrawal(s) left. Are you sure you want to end the challenge before completing it?",
            "End Contract"
        ),

        Triple(
            "Last Chance to Keep Going 🏃",
            "Progress has already been made, and every kilometer counts. You only have {{ withdrawLimitRemaining }} withdrawal(s) remaining for this contract.",
            "I'm Sure"
        ),

        Triple(
            "The Contract Believed in You 💔",
            "There may still be time to complete your goal. You have {{ withdrawLimitRemaining }} withdrawal(s) left. Once withdrawn, this contract can't continue.",
            "Withdraw"
        ),

        Triple(
            "Not Feeling It Today? 🌧️",
            "That's okay, but your future self might regret this. You still have {{ withdrawLimitRemaining }} withdrawal(s) remaining.",
            "Yes, Withdraw"
        )
    )

    val noWithdrawalMessages = listOf(
        Triple(
            "No Withdrawals Left 🚫",
            "You’ve used all your withdrawal chances for this contract. The only way forward now is to complete it.",
            "Understood"
        ),

        Triple(
            "Locked In 🔒",
            "There are no withdrawal attempts remaining. This contract must now be completed before it can end.",
            "Got It"
        ),

        Triple(
            "No Way Out 🏁",
            "All withdrawal options are exhausted. The finish line is now the only exit for this challenge.",
            "Continue"
        ),

        Triple(
            "Commitment Mode Active ⚠️",
            "You’ve reached your withdrawal limit. The contract is now locked until completion.",
            "I Understand"
        ),

        Triple(
            "Final Stretch Only 🏃",
            "No more withdrawals left. Every remaining kilometer now brings you closer to completing this contract.",
            "Keep Going"
        ),

        Triple(
            "No Second Chances 💔",
            "All withdrawal chances are gone for this contract. The goal now is simple: finish it.",
            "Continue"
        )
    )
}

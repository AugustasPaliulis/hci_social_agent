package furhatos.app.newskill.flow.main

import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.state
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.onResponse

// Custom intent
import furhatos.nlu.Intent
import furhatos.util.Language
class GeweldigIntent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return if (lang == Language.DUTCH) listOf("Geweldig") else listOf()
    }
}

val Phrases: State = state {
    var attempts = 0
    onEntry {
        furhat.say("Repeat after each phrase in Dutch.")
        furhat.say("Geweldig")
        furhat.setInputLanguage(Language.DUTCH)
        furhat.listen()
    }


}

val Great: State = state {
    var attempts = 0
    onEntry {
        furhat.say("'Geweldig' means 'Great")
        furhat.listen()
    }
    onResponse<GeweldigIntent> { furhat.say("You said 'Geweldig', which is 'Great' in Dutch.")
        attempts = 0}

    onResponse {
        attempts++
        if (attempts < 2) {
            furhat.say("That's not quite right. Try again.")
            furhat.listen()
        } else {
            furhat.say("Let's move on to the next phrase.")
            attempts = 0
            // Move to the next phrase here
        }
    }
}
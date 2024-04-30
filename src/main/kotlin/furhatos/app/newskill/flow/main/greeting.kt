package furhatos.app.newskill.flow.main

import furhatos.app.newskill.flow.Parent
import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.onResponse
import furhatos.flow.kotlin.state
import furhatos.nlu.common.Greeting
import furhatos.nlu.common.No
import furhatos.nlu.common.Yes
import furhatos.app.newskill.flow.main.Phrases
import furhatos.nlu.Intent
import furhatos.util.Language

val ListeningGreeting: State = state(Parent) {
    onEntry {
        furhat.setInputLanguage(Language.ENGLISH_GB)
        furhat.listen()
    }

    onResponse<Greeting> {
        furhat.say("Hello! Would you like to learn Dutch?")
        goto(WaitingForAnswer)
    }
}

val WaitingForAnswer: State = state {
    onEntry {
        furhat.listen()
    }
    onResponse<Yes> {
        furhat.say("Great! Let's start with some basic phrases.")
        goto(Phrases)
    }

    onResponse<No> {
        furhat.say("No problem. Let me know if you change your mind.")
        goto(ListeningGreeting)
    }
}


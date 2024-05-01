package furhatos.app.newskill.flow.main

import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.state
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.onResponse

// Custom intents
import furhatos.nlu.Intent
import furhatos.nlu.SimpleIntent
import furhatos.util.Language
import kotlin.reflect.KClass

// Intent for Hallo
class HalloIntent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return if (lang == Language.DUTCH) listOf("Hallo") else listOf()
    }
}

// Intent for Goedemorgen
class GoedemorgenIntent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return if (lang == Language.DUTCH) listOf("Goedemorgen") else listOf()
    }
}

// Intent for Goedemiddag
class GoedemiddagIntent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return if (lang == Language.DUTCH) listOf("Goedemiddag") else listOf()
    }
}

// Intent for Geweldig
class GeweldigIntent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return if (lang == Language.DUTCH) listOf("Geweldig") else listOf()
    }
}

val Phrases: State = state {
    var attempts = 0
    onEntry {
        furhat.say("Repeat after each phrase in Dutch.")
        furhat.say("We will start with greetings.")
        call(PhraseState("Hallo", "Hello"))
        call(PhraseState("Goedemiddag", "Good afternoon"))
        call(PhraseState("Goedenavond", "Good evening"))
        call(PhraseState("Goedenavond", "Good evening"))
        call(PhraseState("Tot ziens", "Goodbye"))
        furhat.say("Nice job! Let's move on to the next section, basic phrases.")
        call(PhraseState("Ja", "Yes"))
        call(PhraseState("Nee", "No"))
        call(PhraseState("Alsjeblieft", "Please"))
        call(PhraseState("Dank je wel", "Thank you"))
        call(PhraseState("Sorry", "Excuse me"))
        call(PhraseState("Het spijt me", "I'm sorry"))
    }
}

val Hallo: State = state {
    var attempts = 0
    onEntry {
        furhat.say("'Hallo' means 'Hello")
        furhat.setInputLanguage(Language.DUTCH)
        furhat.listen()
    }
    onResponse<HalloIntent> {
        furhat.say("You said 'Hallo', which is 'Hello' in Dutch.")
        attempts = 0
        goto(Goedemorgen)
    }
    onResponse {
        attempts++
        if (attempts < 2) {
            furhat.say("That's not quite right. Try again.")
            furhat.say("Hallo")
            furhat.listen()
        } else {
            furhat.say("Let's move on to the next phrase.")
            attempts = 0
            goto(Goedemorgen)
        }
    }
}

val Goedemorgen: State = state {
    var attempts = 0
    onEntry {
        furhat.say("'Goedemorgen'")
        furhat.say("Good morning")
        furhat.setInputLanguage(Language.DUTCH)
        furhat.listen()
    }
    onResponse<GoedemorgenIntent> {
        furhat.say("You said 'Goedemorgen', which is 'Good morning' in Dutch.")
        attempts = 0
        goto(Goedemiddag)
    }
    onResponse {
        attempts++
        if (attempts < 2) {
            furhat.say("That's not quite right. Try again.")
            furhat.say("Goedemorgen")
            furhat.listen()
        } else {
            furhat.say("Let's move on to the next phrase.")
            attempts = 0
            goto(Goedemiddag)
        }
    }
}

val Goedemiddag: State = state {
    var attempts = 0
    onEntry {
        furhat.say("'Goedemiddag' means 'Good afternoon")
        furhat.setInputLanguage(Language.DUTCH)
        furhat.listen()
    }
    onResponse<GoedemiddagIntent> {
        furhat.say("You said 'Goedemiddag', which is 'Good afternoon' in Dutch.")
        attempts = 0
        goto(Geweldig)
    }
    onResponse {
        attempts++
        if (attempts < 2) {
            furhat.say("That's not quite right. Try again.")
            furhat.say("Goedemiddag")
            furhat.listen()
        } else {
            furhat.say("Let's move on to the next phrase.")
            attempts = 0
            goto(Geweldig)
        }
    }
}

val Geweldig: State = state {
    var attempts = 0
    onEntry {
        furhat.say("'Geweldig' means 'Great")
        furhat.setInputLanguage(Language.DUTCH)
        furhat.listen()
    }
    onResponse<GeweldigIntent> { furhat.say("You said 'Geweldig', which is 'Great' in Dutch.")
        attempts = 0}
    onResponse {
        attempts++
        if (attempts < 2) {
            furhat.say("That's not quite right. Try again.")
            furhat.say("Geweldig")
            furhat.listen()
        } else {
            furhat.say("Let's move on to the next phrase.")
            attempts = 0
            // Move to the next phrase here
        }
    }
}

fun PhraseState(phrase: String, meaning: String) = state {
    var attempts = 0
    onEntry {
        // Saying phrase and meaning
        furhat.say(phrase)
        furhat.say(meaning)
        // Setting input language to Dutch
        furhat.setInputLanguage(Language.DUTCH)
        furhat.listen()
    }
    val Intent = SimpleIntent(phrase)
    onResponse(Intent) {
        // Correct response
        furhat.say("You said $phrase, which is $meaning in Dutch.")
        attempts = 0
        terminate()
    }
    onResponse {
        // Incorrect response
        // Two attempts allowed
        attempts++
        if (attempts < 2) {
            furhat.say("That's not quite right. Try again.")
            furhat.say(phrase)
            furhat.listen()
        } else {
            furhat.say("Let's move on to the next phrase.")
            attempts = 0
            terminate()
        }
    }
}

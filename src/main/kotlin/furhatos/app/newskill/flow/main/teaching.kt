package furhatos.app.newskill.flow.main

import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.state
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.onResponse

/**Custom intent classes */
import furhatos.nlu.Intent
import furhatos.nlu.SimpleIntent
import furhatos.util.Language

// Intent for age numbers
class AgeNumberIntent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return if (lang == Language.ENGLISH_GB) {
            listOf("18", "19", "20", "21", "22", "23")
        } else {
            listOf()
        }
    }
}

// Intent for university year
class UniversityYearIntent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return if (lang == Language.ENGLISH_GB) {
            listOf("first", "second", "third", "fourth")
        } else {
            listOf()
        }
    }
}

/**
 * Translations objects for translating user answer to Dutch
 * */
// Age number translation
val numberTranslation = mapOf(
    "18" to "achttien",
    "19" to "negentien",
    "20" to "twintig",
    "21" to "eenentwintig",
    "22" to "tweeëntwintig",
    "23" to "drieëntwintig",
)

// University year translation
val yearTranslation = mapOf(
    "first" to "eerste",
    "second" to "tweede",
    "third" to "derde",
    "fourth" to "vierde",
)


// State for teaching phrases
val Phrases: State = state {
    onEntry {
        furhat.say("Repeat after each phrase in Dutch.")
        goto(Questions)
//        furhat.say("We will start with greetings.")
//        call(PhraseState("Hallo", "Hello"))
//        call(PhraseState("Goedemiddag", "Good afternoon"))
//        call(PhraseState("Goedenavond", "Good evening"))
//        call(PhraseState("Goedenavond", "Good evening"))
//        call(PhraseState("Tot ziens", "Goodbye"))
//        furhat.say("Nice job! Let's move on to the next section, basic phrases.")
//        call(PhraseState("Ja", "Yes"))
//        call(PhraseState("Nee", "No"))
//        call(PhraseState("Alsjeblieft", "Please"))
//        call(PhraseState("Dank je wel", "Thank you"))
//        call(PhraseState("Sorry", "Excuse me"))
//        call(PhraseState("Het spijt me", "I'm sorry"))
    }
}

val Questions: State = state {
    onEntry {
        furhat.say("Now, let's practice some questions.")
        furhat.say("I will ask you question in English, please answer in English and then I will teach you to answer this question in Dutch.")
        call(AgeQuestion)
    }
}

// States for teaching questions
val AgeQuestion: State = state {
    onEntry {
        furhat.say("How old are you?")
        furhat.setInputLanguage(Language.ENGLISH_GB)
        furhat.listen()
    }
    onResponse<AgeNumberIntent> {
        val age = it.text.toString()
        val translation = numberTranslation[age]
        if (translation != null) {
            furhat.say("You said $age, which is $translation in Dutch.")
            furhat.say("You could answer this question by saying 'Ik ben $translation jaar oud'.")
            furhat.say("Try saying it now.")
            call(ListenForAnswer("Ik ben $translation jaar oud"))
        } else {
            furhat.say("I'm sorry, I don't know how to say $age in Dutch.")
            furhat.say("Let's start again")
            reentry()
        }
        terminate()
    }
    onReentry { furhat.listen() }
}

val UniversityYearQuestion: State = state {
    onEntry {
        furhat.say("What year are you in at university?")
        furhat.setInputLanguage(Language.ENGLISH_GB)
        furhat.listen()
    }
    onResponse<UniversityYearIntent> {
        val year = it.text.toString()
        val translation = yearTranslation[year]
        if (translation != null) {
            furhat.say("You said $year, which is $translation in Dutch.")
            furhat.say("You could ask this question by saying 'In welk jaar zit je op de universiteit?'.")
            furhat.say("An then you could answer this question by saying 'Ik zit in het $translation jaar van de universiteit'.")
        } else {
            furhat.say("I'm sorry, I don't know how to say $year in Dutch.")
            furhat.say("Let's try again")
            reentry()
        }
        furhat.say("You said $year.")
        furhat.say("You could answer this question by saying 'Ik zit in het $year jaar van de universiteit'.")
        terminate()
    }
    onResponse {
        furhat.say("I'm sorry, I didn't understand that. Could you please repeat by just saying number of what year are you in?")
        reentry()
    }
    onReentry { furhat.listen() }
}


/**
 * State functions
 * */
// Function for teaching phrases
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

fun ListenForAnswer(phrase: String) = state {
    var attempts = 0
    onEntry {
        furhat.setInputLanguage(Language.DUTCH)
        furhat.listen()
    }
    val Intent = SimpleIntent(phrase)
    onResponse(Intent) {
        furhat.say("You said $phrase.")
        furhat.say("Great job!")
        attempts = 0
        terminate()
    }
    onResponse {
        attempts++
        if (attempts < 2) {
            furhat.say("I'm sorry, I didn't understand that. Try saying $phrase")
            furhat.listen()
        } else {
            furhat.say("Let's move on to the next question.")
            attempts = 0
            terminate()
        }
    }
    onReentry { furhat.listen() }
}
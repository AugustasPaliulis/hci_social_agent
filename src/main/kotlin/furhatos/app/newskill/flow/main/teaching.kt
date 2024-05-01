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
// Intent for not understanding
class NotUnderstandIntent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return if (lang == Language.ENGLISH_GB) {
            listOf("I don't understand", "I don't know", "I'm not sure", "Can you repeat?")
        } else {
            listOf()
        }
    }
}
// University course intent
class UniversityStudyIntent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return if (lang == Language.DUTCH) {
            listOf(
                "Ik studeer informatica", // I study computer science
                "Ik studeer wiskunde", // I study mathematics
                "Ik studeer natuurkunde", // I study physics
                "Ik studeer kunstmatige intelligentie", // I study artificial intelligence
                "Ik studeer bedrijfseconomie", // I study business economics
                "Ik studeer biologie", // I study biology
                "Ik studeer scheikunde", // I study chemistry
                "Ik studeer geneeskunde", // I study medicine
                "Ik studeer psychologie", // I study psychology
                "Ik studeer rechten" // I study law
            )
        } else {
            listOf()
        }
    }
}
// Netherlands city intent
class CityIntent : Intent() {
    // List of cities
    private val cities = listOf(
        "Amsterdam",
        "Rotterdam",
        "Den Haag",
        "Utrecht",
        "Eindhoven",
        "Tilburg",
        "Groningen",
        "Almere",
        "Breda",
        "Nijmegen"
    )
    // Creating intent list with cities
    override fun getExamples(lang: Language): List<String> {
        return if (lang == Language.DUTCH) {
            cities.map { "Ik woon in $it" }
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

/**Main flow states*/
// Flow state for teaching phrases
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

// Flow state for teaching questions
val Questions: State = state {
    onEntry {
        furhat.say("Now, let's practice some questions.")
        furhat.say("I will ask you question in English, please answer in English and then I will teach you to answer this question in Dutch.")
        call(AgeQuestion)
        call(UniversityYearQuestion)
        furhat.say("Great job! You have learned how to ask and answer questions in Dutch.")
        furhat.say("Now I will ask some basic questions in Dutch, try to answer them, If you will not understand, say it and I will help you.")
        call(UniversityCourseQuestion)
        call(CityQuestion)

    }
}

/**Callable states for teaching questions*/
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
            call(ListenForPhrase("Ik ben $translation jaar oud"))
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
            // Teaching how to ask the question
            furhat.say("You could ask this question by saying 'In welk jaar zit je op de universiteit?'.")
            furhat.say("Try saying it now.")
            call(ListenForPhrase("In welk jaar zit je op de universiteit?"))
            // Teaching how to answer the question
            furhat.say("An then you could answer this question by saying 'Ik zit in het $translation jaar van de universiteit'.")
            furhat.say("Try saying it now.")
            call(ListenForPhrase("Ik zit in het $translation jaar van de universiteit"))
        } else {
            furhat.say("I'm sorry, I don't know how to say $year in Dutch.")
            furhat.say("Let's try again")
            reentry()
        }

        terminate()
    }

    onResponse {
        furhat.say("I'm sorry, I didn't understand that. Could you please repeat by just saying number of what year are you in?")
        reentry()
    }
    onReentry { furhat.listen() }
}

/** Asking Dutch questions for practice callable states*/
val UniversityCourseQuestion: State = state {
    var attempts = 0
    onEntry {
        furhat.say("Wat studeer je aan de universiteit?")
        furhat.setInputLanguage(Language.DUTCH, Language.ENGLISH_GB)
        furhat.listen()
    }
    onResponse<UniversityStudyIntent> {
        furhat.say("You said ${it.text}.")
        furhat.say("Great job!")
        attempts = 0
        terminate()
    }
    onResponse<NotUnderstandIntent> {
        furhat.say("Wat studeer je aan de universiteit? means what do you study at the university?")
        furhat.say("Try saying 'Ik studeer' and then the name of the course you study.")
        reentry()
    }
    onResponse {
        attempts++
        if (attempts < 2) {
            furhat.say("I didn't understand that. Could you please repeat?")
            furhat.say("If you don't understand, you can say 'I don't understand'.")
            reentry()
        } else {
            furhat.say("Let's move on to the next question.")
            attempts = 0
            terminate()
        }
    }
    onReentry { furhat.listen() }

}

val CityQuestion: State = state {
    var attempts = 0
    onEntry {
        furhat.say("In welke stad woon je?")
        furhat.setInputLanguage(Language.DUTCH, Language.ENGLISH_GB)
        furhat.listen()
    }
    onResponse<CityIntent> {
        furhat.say("You said ${it.text}.")
        furhat.say("Great job!")
        attempts = 0
        terminate()
    }
    onResponse<NotUnderstandIntent> {
        furhat.say("In wlke stad woon je? means in which city do you live?")
        furhat.say("Try saying 'Ik woon in' and then name of the city you live in.")
        reentry()
    }
    onResponse {
        attempts++
        if (attempts < 2) {
            furhat.say("I didn't understand that. Could you please repeat?")
            furhat.say("If you don't understand, you can say 'I don't understand'.")
            reentry()
        } else {
            furhat.say("Let's move on to the next question.")
            attempts = 0
            terminate()
        }
    }
    onReentry { furhat.listen() }
}

/**State functions*/
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
    val intent = SimpleIntent(phrase)
    onResponse(intent) {
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
// Function for listening for a specific provided phrase
fun ListenForPhrase(phrase: String) = state {
    var attempts = 0
    onEntry {
        furhat.setInputLanguage(Language.DUTCH)
        furhat.listen()
    }
    val intent = SimpleIntent(phrase)
    onResponse(intent) {
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
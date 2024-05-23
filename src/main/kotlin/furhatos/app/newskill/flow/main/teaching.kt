package furhatos.app.newskill.flow.main

import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.state
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.onResponse
import furhatos.flow.kotlin.voice.Voice

/**Custom intent classes */
import furhatos.nlu.Intent
import furhatos.nlu.SimpleIntent
import furhatos.nlu.common.DontKnow
import furhatos.util.Gender
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
class DontKnowIntent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return if (lang == Language.ENGLISH_GB) {
            listOf("I don't understand", "I don't know", "I'm not sure")
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
                "Ik studeer AI", // I study artificial intelligence
                "Ik studeer Bedrijf", // I study business
                "Ik studeer economie", // I study economics
                "Ik studeer biologie", // I study biology
                "Ik studeer geneeskunde", // I study medicine
            )}
        else {
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
        return cities.map { "Ik woon in $it" }
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
        furhat.voice = Voice(gender = Gender.FEMALE, language = Language.ENGLISH_GB, rate = 1.0)
        furhat.say("Repeat after each phrase in Dutch.")

        furhat.say("We will start with greetings.")
        call(PhraseState("Hallo", "Hello"))
        call(PhraseState("Goedemorgen", "Good morning"))
        call(PhraseState("Goedemiddag", "Good afternoon"))
        call(PhraseState("Goedenavond", "Good evening"))
        call(PhraseState("Tot ziens", "Goodbye"))
        furhat.say("Nice job! Let's move on to the next section, university course names.")
        call(PhraseState("Wat studeer je?", "What are you studying?"))
        call(PhraseState("Ik studeer Informatica", "I am studying Computer Science"))
        call(PhraseState("Ik studeer Wiskunde", "I am studying Mathematics"))
        call(PhraseState("Ik studeer atuurkunde", "I am studying Physics"))
        call(PhraseState("Ik studeer Bedrijf", "I am studying Business"))
        call(PhraseState("Ik studeer Economie", "I am studying Economics"))
        call(PhraseState("Ik studeer Biologie", "I am studying Biology"))
        call(PhraseState("Ik studeer Geneeskunde", "I am studying Medicine"))
        furhat.say("Nice job! Let's move onto introduction phrases")
        call(PhraseState("Mijn naam is", "My name is"))
        call(PhraseState("Hoe heet je?", "What is your name?"))
        call(PhraseState("Ik kom uit", "I'm from"))
        call(PhraseState("Waar kom je vandaan?", "Where are you from?"))
        call(PhraseState("In welke stad woon je?", "In which city do you live?"))
        goto(Questions)
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
        furhat.say("Great job! You have learned how to ask and answer questions in Dutch.")
        furhat.say("You have completed the lesson.")
        furhat.say("Have a good day!")
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
            furhat.say("You said $age, which is:")
            furhat.voice = Voice(gender = Gender.FEMALE ,language = Language.DUTCH, rate = 0.7)
            furhat.say(translation)
            furhat.voice = Voice(gender = Gender.FEMALE, language = Language.ENGLISH_GB, rate = 1.0)
            furhat.say("in Dutch.")
            furhat.say("You could answer this question by saying:")
            furhat.voice = Voice(gender = Gender.FEMALE, language = Language.DUTCH, rate = 0.7)
            furhat.say("Ik ben $translation jaar oud")
            furhat.voice = Voice(gender = Gender.FEMALE, language = Language.ENGLISH_GB, rate = 1.0)
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
            furhat.say("You said $year, which is:")
            furhat.voice = Voice(gender = Gender.FEMALE, language = Language.DUTCH, rate = 0.7)
            furhat.say(translation)
            furhat.voice = Voice(gender = Gender.FEMALE, language = Language.ENGLISH_GB, rate = 1.0)
            furhat.say("in Dutch.")

            // Teaching how to ask the question
            /*furhat.say("You could ask this question by saying 'In welk jaar zit je op de universiteit?'.")
            furhat.say("Try saying it now.")
            call(ListenForPhrase("In welk jaar zit je op de universiteit?"))*/

            // Teaching how to answer the question
            furhat.say("You could answer this question by saying:")
            furhat.voice = Voice(gender = Gender.FEMALE, language = Language.DUTCH, rate = 0.7)
            furhat.say("Ik zit in het $translation jaar van de universiteit")
            furhat.voice = Voice(gender = Gender.FEMALE, language = Language.ENGLISH_GB, rate = 1.0)
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
        furhat.voice = Voice(gender = Gender.FEMALE, language = Language.DUTCH, rate = 0.7)
        furhat.say("wat studeer je?")
        furhat.voice = Voice(gender = Gender.FEMALE, language = Language.ENGLISH_GB, rate = 1.0)
        furhat.setInputLanguage(Language.ENGLISH_GB, Language.DUTCH)
        furhat.listen()
    }

    onResponse<UniversityStudyIntent> {
        furhat.say("You said:")
        furhat.voice = Voice(gender = Gender.FEMALE, language = Language.DUTCH, rate = 0.7)
        furhat.say(it.text)
        furhat.voice = Voice(gender = Gender.FEMALE, language = Language.ENGLISH_GB, rate = 1.0)
        furhat.say("Great job!")
        attempts = 0
        terminate()
    }

    onResponse<DontKnow> {
        furhat.voice = Voice(gender = Gender.FEMALE, language = Language.DUTCH, rate = 0.7)
        furhat.say("wat studeer je?")
        furhat.voice = Voice(gender = Gender.FEMALE, language = Language.ENGLISH_GB, rate = 1.0)
        furhat.say("means what do you study?")
        furhat.say("Try saying: ")
        furhat.voice = Voice(gender = Gender.FEMALE, language = Language.DUTCH, rate = 0.7)
        furhat.say("Ik studeer")
        furhat.voice = Voice(gender = Gender.FEMALE, language = Language.ENGLISH_GB, rate = 1.0)
        furhat.say("and then the name of the course you study.")
        reentry()
    }
    onResponse {
        attempts++
        if (attempts < 2) {
            furhat.say("I didn't understand that. Could you please repeat?")
            furhat.say("If you don't understand, you can say 'I don't understand'.")
            reentry()
        } else {
            furhat.say("That is not correct, let's move on to the next question.")
            attempts = 0
            terminate()
        }
    }
    onReentry { furhat.listen() }

}

val CityQuestion: State = state {
    var attempts = 0
    onEntry {
        furhat.voice = Voice(gender = Gender.FEMALE, language = Language.DUTCH, rate = 0.7)
        furhat.say("In welke stad woon je?")
        furhat.voice = Voice(gender = Gender.FEMALE, language = Language.ENGLISH_GB, rate = 1.0)
        furhat.setInputLanguage(Language.DUTCH, Language.ENGLISH_GB)
        furhat.listen()
    }

    onResponse<CityIntent> {
        furhat.say("You said:")
        furhat.voice = Voice(gender = Gender.FEMALE, language = Language.DUTCH, rate = 0.7)
        furhat.say(it.text)
        furhat.voice = Voice(gender = Gender.FEMALE, language = Language.ENGLISH_GB, rate = 1.0)
        furhat.say("Great job!")
        attempts = 0
        terminate()
    }
    onResponse<DontKnow> {
        furhat.voice = Voice(gender = Gender.FEMALE, language = Language.DUTCH, rate = 0.7)
        furhat.say("In welke stad woon je?")
        furhat.voice = Voice(gender = Gender.FEMALE, language = Language.ENGLISH_GB, rate = 1.0)
        furhat.say("means in which city do you live?")
        furhat.say("Try saying: ")
        furhat.voice = Voice(gender = Gender.FEMALE, language = Language.DUTCH, rate = 0.7)
        furhat.say("Ik woon in")
        furhat.voice = Voice(gender = Gender.FEMALE, language = Language.ENGLISH_GB, rate = 1.0)
        furhat.say("and then name of the city you live in.")
        reentry()
    }
    onResponse {
        attempts++
        if (attempts < 2) {
            furhat.say("I didn't understand that. Could you please repeat?")
            furhat.say("If you don't understand, you can say 'I don't understand'.")
            reentry()
        } else {
            furhat.say("That is not correct.")
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
        furhat.voice = Voice(gender = Gender.FEMALE, language = Language.DUTCH, rate = 0.7)
        furhat.say(phrase)
        furhat.voice = Voice(gender = Gender.FEMALE, language = Language.ENGLISH_GB, rate = 1.0)
        furhat.say(meaning)
        // Setting input language to Dutch
        furhat.setInputLanguage(Language.DUTCH)
        furhat.listen()
    }
    val intent = SimpleIntent(phrase)
    onResponse(intent) {
        // Correct response
        furhat.say("Great job! You said:")
        furhat.voice = Voice(gender = Gender.FEMALE, language = Language.DUTCH, rate = 0.7)
        furhat.say(phrase)
        furhat.voice = Voice(gender = Gender.FEMALE, language = Language.ENGLISH_GB, rate = 1.0)
        furhat.say(" Which means: $meaning")
        attempts = 0
        terminate()
    }
    onResponse {
        // Incorrect response
        // Two attempts allowed
        attempts++
        if (attempts < 2) {
            furhat.say("That's not quite right. Try again.")
            furhat.voice = Voice(gender = Gender.FEMALE, language = Language.DUTCH, rate = 0.7)
            furhat.say(phrase)
            furhat.voice = Voice(gender = Gender.FEMALE, language = Language.ENGLISH_GB, rate = 1.0)
            furhat.listen()
        } else {
            furhat.say("that's not quite right. Let's move on.")
            attempts = 0
            terminate()
        }
        terminate()
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
        furhat.say("Great job!")
        furhat.say("You said:")
        furhat.voice = Voice(gender = Gender.FEMALE, language = Language.DUTCH, rate = 0.7)
        furhat.say(phrase)
        furhat.voice = Voice(gender = Gender.FEMALE, language = Language.ENGLISH_GB, rate = 1.0)
        attempts = 0
        terminate()
    }
    onResponse {
        attempts++
        if (attempts < 2) {
            furhat.say("I'm sorry, I didn't understand that. Try saying:")
            furhat.voice = Voice(gender = Gender.FEMALE, language = Language.DUTCH, rate = 0.7)
            furhat.say(phrase)
            furhat.voice = Voice(gender = Gender.FEMALE, language = Language.ENGLISH_GB, rate = 1.0)
            furhat.listen()
        } else {
            furhat.say("That is not correct. Let's continue.")
            attempts = 0
            terminate()
        }
    }
    onReentry { furhat.listen() }
}
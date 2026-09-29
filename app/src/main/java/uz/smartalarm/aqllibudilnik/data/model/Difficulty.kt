package uz.smartalarm.aqllibudilnik.data.model

enum class Difficulty(val displayName: String, val description: String) {
    EASY("Oson", "Kichik sonlar va 1 ta amal (+, -, ×, ÷)"),
    MEDIUM("O‘rta", "2 ta aralash amal (ustuvorlik bilan)"),
    HARD("Qiyin", "Murakkab amallar va kattaroq sonlar")
}

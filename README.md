# Language Learning Flashcards App - LLF

## Demo Video
<p align="center">
  <a href="https://www.youtube.com/watch?v=XFQaZ5vmHfo">
    <img src="https://img.youtube.com/vi/XFQaZ5vmHfo/0.jpg" alt="Demo Video" width="560" height="315">
  </a>
</p>

## Overview
The Language Flashcards App LLF is a desktop application developed in Java in a team of 4, designed to help users learn Dutch through interactive exercises and gamification techniques. This app targets English-speaking users who want to improve their Dutch language skills in an engaging and enjoyable way.

## Features
### 1. Level Management
<ul>
      <li>Create, Update, and Delete Flashcards: Users can create custom flashcards using a built-in tool. The app ensures that no incomplete flashcards can be added. Users can also delete or update existing flashcards or levels as needed.</li>
      <li>Custom Flashcards: Users can add new levels and flashcards tailored to their learning needs, enhancing their vocabulary as they progress.</li>
</ul>

### 2. JSON Persistence
<ul>
      <li>Export and Import Flashcards: Users can export selected flashcards as JSON files for easy sharing and import flashcards to build or modify custom levels.</li>
      <li>Easy Sharing: The JSON files make it simple to share custom flashcards among users via online platforms.</li>
</ul>

### 3. Gamification
<ul>
      <li>Difficulty System: The app includes a difficulty system that makes learning more challenging as the user progresses.</li>
      <li>Scoring System: Points are awarded based on correct answers. Users earn 1 point for each correct answer and 0 points for incorrect ones.</li>
      <li>Streak System: A streak multiplier increases with consecutive correct answers, boosting the score. The multiplier resets to 1x after a mistake.</li>
</ul>

### 4. Achievement Badges
<ul>
      <li>Reward System: Users earn badges for achieving specific milestones, such as reaching a certain streak, completing advanced levels, or finishing a level without mistakes.</li>
      <li>Achievement List: Users can view all possible achievements and track their progress.</li>
</ul>

### 5. Listening Exercises
<ul>
      <li>Audio Flashcards: Built-in flashcards include an audio property that plays when activated. Users must type what they hear to earn points.</li>
      <li>AI Integration: The app uses AI text-to-speech models to match audio files with the correct flashcards, improving users' listening skills.</li>
</ul>

### 6. Graphical User Interface (GUI)
<ul>
      <li>User-Friendly Interface: The app provides a main menu with options such as "Resume Game," "New Game," "Custom Games," and "Achievements."</li>
      <li>Interactive Controls: Soft controls like "Play Audio" and "Show Hint" enhance the learning experience, making it easy to navigate and use.</li>
</ul>

### 7. Completion Exercises
<ul>
      <li>Sentence Completion: For flashcards with sentences, random words are removed, and users must fill in the blanks. Correct answers earn points, while incorrect ones push the exercise to the end of the queue.</li>
</ul>

### 8. Cultural/Fun Mode
<ul>
      <li>Idioms and Trivia: This mode focuses on teaching idioms and interesting trivia about the Netherlands, providing a deeper cultural context for language learning.</li>
      <li>Consistent User Interface: The flashcards in this mode are designed similarly to the regular ones, ensuring a cohesive experience.</li>
</ul>

## Technical Details
### Development Stack
**Language:** Java </br>
**GUI Framework:** JavaFX </br>
**JSON Handling:** Jackson library for JSON parsing and generation </br>

### Design Patterns
**MVC (Model-View-Controller):** The application follows the MVC pattern to separate concerns, making it easier to manage, update, and scale the application.

<ul>
      <li>Model: Handles the core logic and data structure (flashcards, levels, user progress).</li>
      <li>View: JavaFX components for rendering the user interface.</li>
      <li>Controller: Manages user inputs and updates the model and view accordingly.</li>
</ul>

**Singleton Pattern:** Used for managing the global state of the application, such as user settings and progress.

**Observer Pattern:** Implements real-time updates between the model and view, ensuring the UI reflects the current state of the data.

**Factory Pattern:** Utilized for creating different types of flashcards (e.g., audio-based, text-based) based on user input or pre-built levels.


## Installation
1. Clone the repository:
```
$ git clone https://github.com/Dolyetyus/Flashcard-App-Learning-Dutch.git
```

3. Navigate to the project directory:
For Windows:
```
$ chdir Flashcard-App-Learning-Dutch
```

For Linux:
```
$ cd Flashcard-App-Learning-Dutch
```

5. Open the project in your preferred Java IDE (e.g., IntelliJ IDEA, Eclipse).
   
6. Build the project and run the application.

</br>
</br>

Developed by:
<ul>
      <li>Alexandru-Valentin Florea </li>
      <li>Mihnea-Andrei Bârsan</li>
      <li>Musab Oğuz</li>
      <li>Robert-Ștefan Sofroni</li>
</ul>

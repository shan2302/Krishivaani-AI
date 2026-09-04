# KrishiVaani AI - Define Git branching strategy and commit conventions

## 1. Repository Structure
    KrishiVaani-AI/
    │
    ├── backend/
    ├── frontend/
    ├── research/
    ├── docs/
    ├── data/
    ├── docker/
    ├── README.md
    ├── .gitignore
    └── pom.xml / package.json / etc.

## 2. Branch strategy
    A branch is basically a separate workspace inside the same repository
    Our team will use:
    main
    │
    ├── feature/shantanu
    ├── feature/vedh
    ├── feature/dheeraj
    └── feature/nitish
    main is the stable/official version of KrishiVaani AI
    It should contain code that is reasonably tested and integrated.
    
    Each person works primarily on their own branch
    for example:
    Shantanu → feature/shantanu
    Vedh     → feature/vedh
    Dheeraj  → feature/dheeraj
    Nitish   → feature/nitish

    Why? --> if someone accidently break the backend --> main remains safe: --> your own branch will get error
## 3. Branch Names for each team member
    Our team should use a consistent naming convention.
    feature/shantanu
    feature/vedh
    feature/dheeraj
    feature/nitish
    For our Project: 
    | Person   | Branch             |
    | -------- | ------------------ |
    | Shantanu | `feature/shantanu` |
    | Vedh     | `feature/vedh`     |
    | Dheeraj  | `feature/dheeraj`  |
    | Nitish   | `feature/nitish`   |

## 4. How to Clone
    Clone means downloading the Github repository to your computer
    a team member gets the project for the first time.
    They Run:- git clone https://github.com/shan2302/KrishiVaani-AI.git
    Then :- cd KrishiVaani-AI
    Now that member have the entire project on their computer
    So all four member clone:-  KrishiVaani-AI

## 5. How to update from main
    This is extremely important for teamwork.
    Suppose Yesterday:- You have 3 things present in your laptop --> main A B C
    Today Dheeraj merged some database changes:- main A B C D
    But your computer still has :- A B C
    You need to download the latest changes
    First:- git checkout feature/your name
    Then:- git pull origin main
    Why do this?
    --> It reduces the chance that your branch becomes outdated and causes difficult conflicts later.

## 6. How to Commit 
    A Commit is basically a saved checkpoint of your work
    First check what changed:- git status
    then tell git which files you want to save :- git add file_name
    then create the commit :- git commit -m "any message related to file_name"
    
## 7. Commit message format
    for example:- docs(KV-001): add research questtion and project scope
    feat-> new functionality
    feat(KV-030): add agricultural document retrieval
    fix -> bug fix
    fix(KV-042): fix invalid login validation
    docs -> documentation
    docs(KV-007): define Git branching conventions
    test -> tests
    test(KV-050): add RAG retrieval tests
    research -> research work
    research(KV-020): evaluate multilingual embeddings
    chore -> configuration/maintenance
    chore(KV-006): configure project gitignore

## 8. How to push
    for pushing-> git push origin feature/your_name
    the flow is
    Your computer
     │
     │ git push
     ↓
    GitHub
     │
     ↓
    feature/your_name

    So:
    git add
    ↓
    git commit
    ↓
    git push
    are three different things
    Simple meaning
    git add     → Select changes

## 9. How to merge into main
    This is where everyone's work eventually comes together
    Suppose you finish your work:- feature/your_name
    you push it:- git push origin feature/your_name
    then on Github you create a Pull Request:
    feature/your_name
       ↓
       PR
       ↓
     main
    All other team mates can review your changes:-
    If everything is okay:
    feature/shantanu
       ↓
    APPROVED
       ↓
      main
    
    Now your changes become part of the official project

    Why Pull Requests?
    Because someone can check:
        Does the code work?
        Did this break anything?
        Is the implementation correct?
        Are tests passing?
        Does it follow the project structure?
    this gives you a nice Github history showing proper collaboration
## 10.  Basic Rules to avoid conflicts

    ### 10.1 Pull before starting work
        git checkout feature/your_name
        git pull origin main
    ### 10.2 Dont modify the same file unnecessarily
        go with your kanvan board present at our distribution website and see your work
        according to that work you have to work...
    ### 10.3 Dont randomly overwrite another person's work
        Before making major changes to shared files,tell the team
    ### 10.4 Pull frequently 
        Dont work for 10 days without updating your branch
        Regularly:- git pull origin main
    ### 10.5 Dont force push
        Avoid :- git push --force
    
    ### 10.6 Use meaningful commits
        Dont write:- update,changes,final,final2,abc
        Instead:- feat(KV-030): implement agricultural document retrieval
    ### 10.7 Never commit secrets
        Never commit:- .env,API keys,passwords,database credentials,OAuth secrets,private tokens
        Your .gitignore should protect these files


    Jira Task
    ↓
    Work on your branch
    ↓
    git add
    ↓
    git commit
    ↓
    git push
    ↓
    Pull Request
    ↓
    Review
    ↓
    Merge
    ↓
    main
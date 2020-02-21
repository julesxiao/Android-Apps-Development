Black Jack
Jieqiong Xiao 465180 
Haiyu Wan 458812
Notice:
1. Because our creative portion has the bet function, so the player must hit the bet button first so the bet would be fixed. Then the player would be able to use gestures to choose hit or stand.
2. For this rubric: A new round is automatically started after each round
We use snackbar to notify the player if they lose or win. So the player has to choose to close to snackbar before the new round will automatically start.
3. If player doesn’t play any round, the player will not show up in the leader board

Creative portion:
1. Bet Function
Each player will be given 5000$ at the beginning. The player would choose the decrease and increase button to change the bet.After you click the bet button, it will not allow you to increase or decrease bet. If the bet chosen by the player is over their balance, they could not increase the bet any more.
If the player wins the round, the money would be added to their bank account in the game. If the player loses the round, the money would be subtracted from their bank account. The player's money amount are displayed on startup and updated in the Firebase each round and showed on the play page accordingly.
If the player runs out of money in the current round, the player will get 5000$ at the new round.
2. Background Music
During the running procedure of the app, the player would hear the background music(including the create account page and sign in page). Once the player is login in, they could choose to click the Pause Music button to pause the music, then they could choose to click the Start Music button to restart the music.
When the player changes from one activity to another, the background music would not be interrupted, which is implemented by Service, and we make sure the music would not be interrupted by using Service.START_STICKY.

110/110

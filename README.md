# mark
a chat application
# technologies used
- Spring Boot,JWT
- Openfire,Smack
- Postgres,DBConnection Pooling
- WebSockets
# working video
video - https://www.youtube.com/watch?v=_7Da1ulTjRQ
# features
- users can login,register,view friendsList,view friendRequestsList.
- accept or deny friendRequests
- with every login/register a request is sent to XMPP server to login/register the user.
- a websocket connection opens when user connects to a user
- every message is stored in DB
- messages are retrieved from DB on reconnection.

(the code here is very verbose and i like it :) )


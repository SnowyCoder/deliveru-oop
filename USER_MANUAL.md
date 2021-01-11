# DeliverU User Manual
This is the user manual of DeliverU.

## Introduction
This application permits the user to manage deliveries, from both an admin and an
user perspective. The deliveries have an unique code (generated randomly), an
insertion date, a destination and a weight. Users can be created on the fly and
their password will be encoded and saved along with the other details.
An user can create a delivery and view all of its deliveries, an administrator can
view every delivery present in the system and change its state.

## User Interface
Upon entering the program you will be asked whether to log in as admin or
as a normal user, in both cases the login screen will be shown with some
differences. The admin login screen only has an username and a password while
the user login screen also has the possibility to register new users and another
field with the user address (required on registration). For more details on
the registration process check "User Management".
The administrator credentials are hard-coded in the program, the default ones
are "admin", "password".
When the login or registration is complete you will be redirected to the main
screen. From here you can see all your deliveries (or all the available
  deliveries if you're an administrator) and you can also create new ones.
For more details on the creation and management of the deliveries check
"Delivery Management".

## User Management
Upon login the user can also register with new credentials.
To do that you can just check the "I'm new" box and fill the required fields.
The only requirement for the username is that it should not be already in use.
Clicking the "Register" button will finish with the registration and take you to
the main screen.

## Password Management
The default password storage strategy is a secure one, so it is recommended not
to change it, but you can choose another one by going in the Settings screen
(File->Settings). The available settings are PBKDF2, SHA256 or Plain.
When you change the storage strategy the previous password will not be
re-encoded, only the newly registered users will have their password encoded
with the new storage strategy.

## Delivery Management
An User can create a Delivery by clicking on the "Create" button (on the
  bottom right of the window), it will be asked to fill out some details:
- The delivery type (Normal or Insured)
- The destination
- The weight (in Kg)

If the delivery type is insured the "Insured Value" field will be activated.
The default value of the "Destination" field is the User's address (but it can
  be changed).
Once all the fields are compiled the button "Create" will be enabled, pressing
it will add the delivery to the table.

The only other thing that an unprivileged user can do is to ask for a refund
when an insured delivery fails (by right clicking on a delivery and selecting
 "Request refund").


Admins cannot create new deliveries but they can view all of the deliveries
registered by the other users and remove the deliveries in a final state (by
  right click -> Remove).
If the Automatic Delivery is disabled, the administrator can also change a
delivery state (unless it's already in a final state) by right clicking on a
delivery and selecting "Set state".

## Automatic Delivery
From the Settings panel (File->Settings) you can also enable the Automatic
Delivery by clicking on "Delivery Mode" until the button says "Auto".
This will perform random actions on the deliveries (with an average per minute
  defined by "Delivery actions per minute (avg)") advancing their state.
The delivery can also fail (while passing from the "In transit" to the
"Received" state) with a rate defined by "Fail Rate", in case of failure the
state will be set to "FAILED" and, only if the delivery is insured, the user
will be able to request a refund (check "Delivery Management for more details").

## Save directory
The App will choose its save directory according to the operating system it's
run on. In Unix it will follow the XDG conventions.
- Unix: `~/.config/DeliverU`, `~/.local/share/DeliverU`
- Windows: `C:\Users\<Account>\AppData\DeliverU`
- Mac: `/Users/<Account>/Library/Application Support/DeliverU`

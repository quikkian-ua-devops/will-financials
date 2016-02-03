import KfsUtils from './utils.js';

function getPrincipalName(success,fail) {
    $.ajax({
        url: KfsUtils.getUrlPathPrefix() + "sys/authentication/id",
        dataType: 'json',
        cache: false,
        type: 'GET',
        success: function (principalName) {
            success(principalName.principalName);
        },
        error: function (xhr, status, err) {
            fail(err.toString());
        }
    })
}

function getUserPreferences(success,fail) {
    let p1 = new Promise ( getPrincipalName );

    p1.then(function(principalName) {
        $.ajax({
            url: KfsUtils.getUrlPathPrefix() + "sys/preferences/users/" + principalName,
            dataType: 'json',
            cache: false,
            type: 'GET',
            success: function (userPreferences) {
                success(userPreferences);
            },
            error: function (xhr, status, err) {
                fail(err.toString());
            }
        });
    }).catch(function(message) {
        fail(message);
    });
 }

function putUserPreferences(userPreferences) {
    let p1 = new Promise ( getPrincipalName );

    p1.then(function(principalName) {
        $.ajax({
            url: KfsUtils.getUrlPathPrefix() + "sys/preferences/users/" + principalName,
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(userPreferences),
            cache: false,
            type: 'PUT',
            error: function (xhr, status, err) {
                console.error(status, err.toString());
            }
        });
    }).catch(function(message) {
        console.log("Error: " + message);
    });
}

function getBackdoorId(success, fail) {
    let backdoorIdPath = KfsUtils.getUrlPathPrefix() + "sys/backdoor/id";
    $.ajax({
        url: backdoorIdPath,
        dataType: 'json',
        cache: false,
        type: 'GET',
        success(backdoorIdJson) {
            success(backdoorIdJson['backdoorId']);
        },
        error(xhr, status, err) {
            fail(status, err);
        }
    })
}

const UserPrefs = {
    getPrincipalName: getPrincipalName,
    getUserPreferences: getUserPreferences,
    putUserPreferences: putUserPreferences,
    getBackdoorId: getBackdoorId
}

module.exports = UserPrefs;
export default UserPrefs;

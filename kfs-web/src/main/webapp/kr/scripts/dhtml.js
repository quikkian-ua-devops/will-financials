/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
var closed_file = jsContextPath + "/kr/images/tinybutton-show.gif";
var open_file = jsContextPath + "/kr/images/tinybutton-hide.gif";

img1 = new Image();
img1.src = closed_file;
img2 = new Image();
img2.src = open_file;

function rend(obj, cc) {
    var len = ((String)(obj.id)).indexOf('-',0)-1;
    if (len == -2)
      len = ((String)(obj.id)).length;
    var index = ((String)(obj.id)).substr(1, len);

    if(document.getElementById) {
      var grpIdx = document.getElementById("G"+index);
      var fldIdx = document.getElementById("F"+index);
      var lnkIdx = document.getElementById("A"+index);
    } else if (document.all) {
      var grpIdx = eval("document.all.G"+index);
      var fldIdx = eval("document.all.F"+index);
      var lnkIdx = eval("document.all.A"+index);
    } else {
      alert('This browser is not supported by this tree...');
      return;
    }

    if (grpIdx.style.display == 'none') {
      grpIdx.style.display = '';
      fldIdx.title = 'hide';
      if(cc){
        fldIdx.src = open_file_cc;
      } else {
        fldIdx.src = open_file;
      }
    } else {
      grpIdx.style.display = 'none';
      fldIdx.title = 'show';
      if(cc){
        fldIdx.src = closed_file_cc;
      } else {
        fldIdx.src = closed_file;
      }
    }
    return;
}

function expandAll(doit, cc) {
  var index = 1;
  while (index > 0) {
    if(document.getElementById) {
      var grpIdx = document.getElementById("G"+index);
      var fldIdx = document.getElementById("F"+index);
    } else if (document.all) {
      var grpIdx = eval("document.all.G"+index);
      var fldIdx = eval("document.all.F"+index);
    }
    if (!grpIdx) {
      index = -1;
    } else {
      if (doit == "true") {
        grpIdx.style.display = '';
        if(cc && index == 1){
          fldIdx.src = open_file_cc;
        }else{
          fldIdx.src = open_file;
        }
      } else {
        grpIdx.style.display = 'none';
        if(cc && index == 1){
          fldIdx.src = closed_file_cc;
        }else{
          fldIdx.src = closed_file;
        }
      }
      index++;
    }
  }
}

//
// ANCHOR JAVASCRIPT METHOD
//
function jumpToAnchor(anchorLocation) {
  if(anchorLocation != null){
	document.location.hash = anchorLocation;
  }
}
//
// FIELD GET/SET helper methods
//

var divSuffix = ".div";

String.prototype.trim = function() { return this.replace(/^\s+|\s+$/, ''); };

function findElPrefix( elName ) {
    var prefixIndex = elName.lastIndexOf("." );

    if( prefixIndex < 0 ) {
        prefixIndex = elName.length;
    }
    return elName.substring( 0, prefixIndex );
}

function getElementValue( name ) {
    var el = kualiElements[name];
    el.value = el.value.toUpperCase().trim();

    return el.value;
}

function setElementValue( name, value ) {
    var el = kualiElements[name];
    if ( el ) {
    	if ( el.tagName == "INPUT" ) {
    		if ( el.type == "text" ) {
			    el.value = value;
    		} else if ( el.type == "checkbox" ) {
    			el.checked = (value == true);
    		} else if ( el.type == "radio" ) {
    			if ( el.length ) {
		    		for ( var i = 0; i < el.length; i++ ) {
		    			if ( el[i].value == value ) {
		    				el[i].checked = true;
		    				break;
						}
					}
    			} else {
	    			el.checked = (value == el.value);
    			}
    		} else {
			    el.value = value;
    		}
    	} else if ( el.tagName == "SELECT" ) {
    		for ( var i = 0; i < el.options.length; i++ ) {
    			if ( el.options[i].value == value ) {
    				el.selectedIndex = i;
    				break;
				}
			}
    	} else if ( el.tagName == "TEXTAREA" ) {
		    el.value = value;
		}
    }
}

var previousKualiElementValues = new Object();

function valueChanged( name ) {
    var previousValue = previousKualiElementValues[ name ];
    var currentValue = getElementValue( name );
    previousKualiElementValues[ name ] = currentValue;
    // undefined (i.e., null) is not considered changed, for the sake of newly added accounting lines
    return previousValue != null && previousValue != currentValue;
}

function wrapError( msg ) {
	return "<span style='color: red;'>" + msg + "</span>";
}

function clearRecipients(recipientBase) {
	setRecipientValue(recipientBase, "");
}

function setRecipientValue(recipientBase, value, isError ) {
    // Trim because leading whitespace from copyright comment interferes with putting value into objectTypeCode input field.
    value = value.toString().trim();
    var containerHidden = document.getElementById(recipientBase);
    if ( !containerHidden ) {
	    containerHidden = kualiElements[recipientBase];
    }
    var containerDiv = document.getElementById(recipientBase + divSuffix);
    if (containerDiv) {
		if (value == '') {
			dwr.util.setValue( containerDiv.id, "&nbsp;", {escapeHtml:false} );
		} else {
            dwr.util.setValue( containerDiv.id, value, isError?{escapeHtml:false}:{escapeHtml:true} );
		}
	}
    if (containerHidden) {
    	// get rid of HTML in the value
    	value = value.replace(/(<([^>]+)>)/ig,"");
        dwr.util.setValue( recipientBase, value );
	}
}


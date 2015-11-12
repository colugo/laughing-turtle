CodeMirror.defineMode("actionlanguage", function() {
    var actionKeywords = ['CREATE','DELETE', 'CANCEL', 'GENERATE', 'RECLASSIFY', 'RELATE', 'SELECT', 'UNRELATE'];
    var actionRE = wordRegexp(actionKeywords);
    var directionKeywords = ['FROM','TO', 'ACROSS'];
    var directionRE = wordRegexp(directionKeywords);
    var blockKeywords = ['IF', 'THEN', 'ELSE', 'FOR','END IF', 'END FOR', 'IN', 'DO'];
    var blockRE = wordRegexp(blockKeywords);
    var generateKeywords = ['CREATOR','DELAY','CREATING', 'INSTANCES OF', 'RELATED BY', 'THAT RELATES'];
    var generateRE = wordRegexp(generateKeywords);
    var timeKeywords = ['milliseconds','seconds','minutes','hours','days','millisecond','second','minute','hour','day'];
    var timeRE = wordRegexpB(timeKeywords);
    var anymanyKeywords = ['ANY','MANY', 'ONE'];
    var anymanyRE = wordRegexp(anymanyKeywords);
    function wordRegexp(words) {
        return new RegExp("^((" + words.join(")|(") + "))\\b");
    }
    function wordRegexpB(words) {
        return new RegExp("^((" + words.join(")|(") + "))");
    }

    function check_Comment(stream, state){
        return stream.sol() && stream.match(/\t* *#.*/);
    }
    function do_Comment(stream, state){
        stream.skipToEnd();
        return "comment";
    }
   
    return {
        startState: function(basecolumn) {
            return {};
        },
        token: function(stream, state) {
            if(stream.sol()){
                state.line = stream.string;
            }

            if(check_Comment(stream, state)){
                return do_Comment(stream, state);
            }

            if(stream.match(actionRE)){
                return "keyword";
            }
            if(stream.match(directionRE)){
                return "builtin";
            }
            if(stream.match(blockRE)){
                return "tag";
            }
            if(stream.match(generateRE)){
                return "def";
            }
            if(stream.match(timeRE)){
                return "variable-2";
            }
            if(stream.match(anymanyRE)){
                return "variable-2";
            }
            if(stream.peek() == ';'){
                stream.next();
                return "atom";
            }
            if(stream.match(/rcvd_event\./)){
                return "qualifier";
            }
            if(stream.match(/NOT./)){
                return "negative";
            }
            if(stream.match(/EMPTY./)){
                return "meta";
            }
            if(stream.match(/RETURN./)){
                return "meta";
            }
            if(stream.match(/\."[^\"]*"/)){
                return "meta";
            }
            if(stream.match(/->/)){
                return "tag";
            }

            stream.next();
            return "string";
        }
    };
});
 
CodeMirror.defineMIME("text/x-actionlanguage", "actionlanguage");


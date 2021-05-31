/*eslint-disable block-scoped-var, id-length, no-control-regex, no-magic-numbers, no-prototype-builtins, no-redeclare, no-shadow, no-var, sort-vars*/
"use strict";

var $protobuf = require("protobufjs/minimal");

// Common aliases
var $Reader = $protobuf.Reader, $Writer = $protobuf.Writer, $util = $protobuf.util;

// Exported root namespace
var $root = $protobuf.roots["default"] || ($protobuf.roots["default"] = {});

/**
 * FOO enum.
 * @exports FOO
 * @enum {number}
 * @property {number} BAR=1 BAR value
 */
$root.FOO = (function() {
    var valuesById = {}, values = Object.create(valuesById);
    values[valuesById[1] = "BAR"] = 1;
    return values;
})();

$root.Test = (function() {

    /**
     * Properties of a Test.
     * @exports ITest
     * @interface ITest
     * @property {number} num Test num
     * @property {string} payload Test payload
     * @property {number|null} [timestamp] Test timestamp
     */

    /**
     * Constructs a new Test.
     * @exports Test
     * @classdesc Represents a Test.
     * @implements ITest
     * @constructor
     * @param {ITest=} [properties] Properties to set
     */
    function Test(properties) {
        if (properties)
            for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                if (properties[keys[i]] != null)
                    this[keys[i]] = properties[keys[i]];
    }

    /**
     * Test num.
     * @member {number} num
     * @memberof Test
     * @instance
     */
    Test.prototype.num = 0;

    /**
     * Test payload.
     * @member {string} payload
     * @memberof Test
     * @instance
     */
    Test.prototype.payload = "";

    /**
     * Test timestamp.
     * @member {number} timestamp
     * @memberof Test
     * @instance
     */
    Test.prototype.timestamp = 0;

    /**
     * Creates a new Test instance using the specified properties.
     * @function create
     * @memberof Test
     * @static
     * @param {ITest=} [properties] Properties to set
     * @returns {Test} Test instance
     */
    Test.create = function create(properties) {
        return new Test(properties);
    };

    /**
     * Encodes the specified Test message. Does not implicitly {@link Test.verify|verify} messages.
     * @function encode
     * @memberof Test
     * @static
     * @param {ITest} message Test message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    Test.encode = function encode(message, writer) {
        if (!writer)
            writer = $Writer.create();
        writer.uint32(/* id 1, wireType 5 =*/13).float(message.num);
        writer.uint32(/* id 2, wireType 2 =*/18).string(message.payload);
        if (message.timestamp != null && Object.hasOwnProperty.call(message, "timestamp"))
            writer.uint32(/* id 10, wireType 1 =*/81).double(message.timestamp);
        return writer;
    };

    /**
     * Encodes the specified Test message, length delimited. Does not implicitly {@link Test.verify|verify} messages.
     * @function encodeDelimited
     * @memberof Test
     * @static
     * @param {ITest} message Test message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    Test.encodeDelimited = function encodeDelimited(message, writer) {
        return this.encode(message, writer).ldelim();
    };

    /**
     * Decodes a Test message from the specified reader or buffer.
     * @function decode
     * @memberof Test
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @param {number} [length] Message length if known beforehand
     * @returns {Test} Test
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    Test.decode = function decode(reader, length) {
        if (!(reader instanceof $Reader))
            reader = $Reader.create(reader);
        var end = length === undefined ? reader.len : reader.pos + length, message = new $root.Test();
        while (reader.pos < end) {
            var tag = reader.uint32();
            switch (tag >>> 3) {
            case 1:
                message.num = reader.float();
                break;
            case 2:
                message.payload = reader.string();
                break;
            case 10:
                message.timestamp = reader.double();
                break;
            default:
                reader.skipType(tag & 7);
                break;
            }
        }
        if (!message.hasOwnProperty("num"))
            throw $util.ProtocolError("missing required 'num'", { instance: message });
        if (!message.hasOwnProperty("payload"))
            throw $util.ProtocolError("missing required 'payload'", { instance: message });
        return message;
    };

    /**
     * Decodes a Test message from the specified reader or buffer, length delimited.
     * @function decodeDelimited
     * @memberof Test
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @returns {Test} Test
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    Test.decodeDelimited = function decodeDelimited(reader) {
        if (!(reader instanceof $Reader))
            reader = new $Reader(reader);
        return this.decode(reader, reader.uint32());
    };

    /**
     * Verifies a Test message.
     * @function verify
     * @memberof Test
     * @static
     * @param {Object.<string,*>} message Plain object to verify
     * @returns {string|null} `null` if valid, otherwise the reason why it is not
     */
    Test.verify = function verify(message) {
        if (typeof message !== "object" || message === null)
            return "object expected";
        if (typeof message.num !== "number")
            return "num: number expected";
        if (!$util.isString(message.payload))
            return "payload: string expected";
        if (message.timestamp != null && message.hasOwnProperty("timestamp"))
            if (typeof message.timestamp !== "number")
                return "timestamp: number expected";
        return null;
    };

    /**
     * Creates a Test message from a plain object. Also converts values to their respective internal types.
     * @function fromObject
     * @memberof Test
     * @static
     * @param {Object.<string,*>} object Plain object
     * @returns {Test} Test
     */
    Test.fromObject = function fromObject(object) {
        if (object instanceof $root.Test)
            return object;
        var message = new $root.Test();
        if (object.num != null)
            message.num = Number(object.num);
        if (object.payload != null)
            message.payload = String(object.payload);
        if (object.timestamp != null)
            message.timestamp = Number(object.timestamp);
        return message;
    };

    /**
     * Creates a plain object from a Test message. Also converts values to other types if specified.
     * @function toObject
     * @memberof Test
     * @static
     * @param {Test} message Test
     * @param {$protobuf.IConversionOptions} [options] Conversion options
     * @returns {Object.<string,*>} Plain object
     */
    Test.toObject = function toObject(message, options) {
        if (!options)
            options = {};
        var object = {};
        if (options.defaults) {
            object.num = 0;
            object.payload = "";
            object.timestamp = 0;
        }
        if (message.num != null && message.hasOwnProperty("num"))
            object.num = options.json && !isFinite(message.num) ? String(message.num) : message.num;
        if (message.payload != null && message.hasOwnProperty("payload"))
            object.payload = message.payload;
        if (message.timestamp != null && message.hasOwnProperty("timestamp"))
            object.timestamp = options.json && !isFinite(message.timestamp) ? String(message.timestamp) : message.timestamp;
        return object;
    };

    /**
     * Converts this Test to JSON.
     * @function toJSON
     * @memberof Test
     * @instance
     * @returns {Object.<string,*>} JSON object
     */
    Test.prototype.toJSON = function toJSON() {
        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
    };

    return Test;
})();

$root.AnotherOne = (function() {

    /**
     * Properties of an AnotherOne.
     * @exports IAnotherOne
     * @interface IAnotherOne
     * @property {Array.<FOO>|null} [list] AnotherOne list
     */

    /**
     * Constructs a new AnotherOne.
     * @exports AnotherOne
     * @classdesc Represents an AnotherOne.
     * @implements IAnotherOne
     * @constructor
     * @param {IAnotherOne=} [properties] Properties to set
     */
    function AnotherOne(properties) {
        this.list = [];
        if (properties)
            for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                if (properties[keys[i]] != null)
                    this[keys[i]] = properties[keys[i]];
    }

    /**
     * AnotherOne list.
     * @member {Array.<FOO>} list
     * @memberof AnotherOne
     * @instance
     */
    AnotherOne.prototype.list = $util.emptyArray;

    /**
     * Creates a new AnotherOne instance using the specified properties.
     * @function create
     * @memberof AnotherOne
     * @static
     * @param {IAnotherOne=} [properties] Properties to set
     * @returns {AnotherOne} AnotherOne instance
     */
    AnotherOne.create = function create(properties) {
        return new AnotherOne(properties);
    };

    /**
     * Encodes the specified AnotherOne message. Does not implicitly {@link AnotherOne.verify|verify} messages.
     * @function encode
     * @memberof AnotherOne
     * @static
     * @param {IAnotherOne} message AnotherOne message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    AnotherOne.encode = function encode(message, writer) {
        if (!writer)
            writer = $Writer.create();
        if (message.list != null && message.list.length)
            for (var i = 0; i < message.list.length; ++i)
                writer.uint32(/* id 1, wireType 0 =*/8).int32(message.list[i]);
        return writer;
    };

    /**
     * Encodes the specified AnotherOne message, length delimited. Does not implicitly {@link AnotherOne.verify|verify} messages.
     * @function encodeDelimited
     * @memberof AnotherOne
     * @static
     * @param {IAnotherOne} message AnotherOne message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    AnotherOne.encodeDelimited = function encodeDelimited(message, writer) {
        return this.encode(message, writer).ldelim();
    };

    /**
     * Decodes an AnotherOne message from the specified reader or buffer.
     * @function decode
     * @memberof AnotherOne
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @param {number} [length] Message length if known beforehand
     * @returns {AnotherOne} AnotherOne
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    AnotherOne.decode = function decode(reader, length) {
        if (!(reader instanceof $Reader))
            reader = $Reader.create(reader);
        var end = length === undefined ? reader.len : reader.pos + length, message = new $root.AnotherOne();
        while (reader.pos < end) {
            var tag = reader.uint32();
            switch (tag >>> 3) {
            case 1:
                if (!(message.list && message.list.length))
                    message.list = [];
                if ((tag & 7) === 2) {
                    var end2 = reader.uint32() + reader.pos;
                    while (reader.pos < end2)
                        message.list.push(reader.int32());
                } else
                    message.list.push(reader.int32());
                break;
            default:
                reader.skipType(tag & 7);
                break;
            }
        }
        return message;
    };

    /**
     * Decodes an AnotherOne message from the specified reader or buffer, length delimited.
     * @function decodeDelimited
     * @memberof AnotherOne
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @returns {AnotherOne} AnotherOne
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    AnotherOne.decodeDelimited = function decodeDelimited(reader) {
        if (!(reader instanceof $Reader))
            reader = new $Reader(reader);
        return this.decode(reader, reader.uint32());
    };

    /**
     * Verifies an AnotherOne message.
     * @function verify
     * @memberof AnotherOne
     * @static
     * @param {Object.<string,*>} message Plain object to verify
     * @returns {string|null} `null` if valid, otherwise the reason why it is not
     */
    AnotherOne.verify = function verify(message) {
        if (typeof message !== "object" || message === null)
            return "object expected";
        if (message.list != null && message.hasOwnProperty("list")) {
            if (!Array.isArray(message.list))
                return "list: array expected";
            for (var i = 0; i < message.list.length; ++i)
                switch (message.list[i]) {
                default:
                    return "list: enum value[] expected";
                case 1:
                    break;
                }
        }
        return null;
    };

    /**
     * Creates an AnotherOne message from a plain object. Also converts values to their respective internal types.
     * @function fromObject
     * @memberof AnotherOne
     * @static
     * @param {Object.<string,*>} object Plain object
     * @returns {AnotherOne} AnotherOne
     */
    AnotherOne.fromObject = function fromObject(object) {
        if (object instanceof $root.AnotherOne)
            return object;
        var message = new $root.AnotherOne();
        if (object.list) {
            if (!Array.isArray(object.list))
                throw TypeError(".AnotherOne.list: array expected");
            message.list = [];
            for (var i = 0; i < object.list.length; ++i)
                switch (object.list[i]) {
                default:
                case "BAR":
                case 1:
                    message.list[i] = 1;
                    break;
                }
        }
        return message;
    };

    /**
     * Creates a plain object from an AnotherOne message. Also converts values to other types if specified.
     * @function toObject
     * @memberof AnotherOne
     * @static
     * @param {AnotherOne} message AnotherOne
     * @param {$protobuf.IConversionOptions} [options] Conversion options
     * @returns {Object.<string,*>} Plain object
     */
    AnotherOne.toObject = function toObject(message, options) {
        if (!options)
            options = {};
        var object = {};
        if (options.arrays || options.defaults)
            object.list = [];
        if (message.list && message.list.length) {
            object.list = [];
            for (var j = 0; j < message.list.length; ++j)
                object.list[j] = options.enums === String ? $root.FOO[message.list[j]] : message.list[j];
        }
        return object;
    };

    /**
     * Converts this AnotherOne to JSON.
     * @function toJSON
     * @memberof AnotherOne
     * @instance
     * @returns {Object.<string,*>} JSON object
     */
    AnotherOne.prototype.toJSON = function toJSON() {
        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
    };

    return AnotherOne;
})();

module.exports = $root;

import * as $protobuf from "protobufjs";
/** FOO enum. */
export enum FOO {
    BAR = 1
}

/** Represents a Test. */
export class Test implements ITest {

    /**
     * Constructs a new Test.
     * @param [properties] Properties to set
     */
    constructor(properties?: ITest);

    /** Test num. */
    public num: number;

    /** Test payload. */
    public payload: string;

    /** Test timestamp. */
    public timestamp: number;

    /**
     * Creates a new Test instance using the specified properties.
     * @param [properties] Properties to set
     * @returns Test instance
     */
    public static create(properties?: ITest): Test;

    /**
     * Encodes the specified Test message. Does not implicitly {@link Test.verify|verify} messages.
     * @param message Test message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encode(message: ITest, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Encodes the specified Test message, length delimited. Does not implicitly {@link Test.verify|verify} messages.
     * @param message Test message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encodeDelimited(message: ITest, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Decodes a Test message from the specified reader or buffer.
     * @param reader Reader or buffer to decode from
     * @param [length] Message length if known beforehand
     * @returns Test
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): Test;

    /**
     * Decodes a Test message from the specified reader or buffer, length delimited.
     * @param reader Reader or buffer to decode from
     * @returns Test
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): Test;

    /**
     * Verifies a Test message.
     * @param message Plain object to verify
     * @returns `null` if valid, otherwise the reason why it is not
     */
    public static verify(message: { [k: string]: any }): (string|null);

    /**
     * Creates a Test message from a plain object. Also converts values to their respective internal types.
     * @param object Plain object
     * @returns Test
     */
    public static fromObject(object: { [k: string]: any }): Test;

    /**
     * Creates a plain object from a Test message. Also converts values to other types if specified.
     * @param message Test
     * @param [options] Conversion options
     * @returns Plain object
     */
    public static toObject(message: Test, options?: $protobuf.IConversionOptions): { [k: string]: any };

    /**
     * Converts this Test to JSON.
     * @returns JSON object
     */
    public toJSON(): { [k: string]: any };
}

/** Represents an AnotherOne. */
export class AnotherOne implements IAnotherOne {

    /**
     * Constructs a new AnotherOne.
     * @param [properties] Properties to set
     */
    constructor(properties?: IAnotherOne);

    /** AnotherOne list. */
    public list: FOO[];

    /**
     * Creates a new AnotherOne instance using the specified properties.
     * @param [properties] Properties to set
     * @returns AnotherOne instance
     */
    public static create(properties?: IAnotherOne): AnotherOne;

    /**
     * Encodes the specified AnotherOne message. Does not implicitly {@link AnotherOne.verify|verify} messages.
     * @param message AnotherOne message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encode(message: IAnotherOne, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Encodes the specified AnotherOne message, length delimited. Does not implicitly {@link AnotherOne.verify|verify} messages.
     * @param message AnotherOne message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encodeDelimited(message: IAnotherOne, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Decodes an AnotherOne message from the specified reader or buffer.
     * @param reader Reader or buffer to decode from
     * @param [length] Message length if known beforehand
     * @returns AnotherOne
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): AnotherOne;

    /**
     * Decodes an AnotherOne message from the specified reader or buffer, length delimited.
     * @param reader Reader or buffer to decode from
     * @returns AnotherOne
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): AnotherOne;

    /**
     * Verifies an AnotherOne message.
     * @param message Plain object to verify
     * @returns `null` if valid, otherwise the reason why it is not
     */
    public static verify(message: { [k: string]: any }): (string|null);

    /**
     * Creates an AnotherOne message from a plain object. Also converts values to their respective internal types.
     * @param object Plain object
     * @returns AnotherOne
     */
    public static fromObject(object: { [k: string]: any }): AnotherOne;

    /**
     * Creates a plain object from an AnotherOne message. Also converts values to other types if specified.
     * @param message AnotherOne
     * @param [options] Conversion options
     * @returns Plain object
     */
    public static toObject(message: AnotherOne, options?: $protobuf.IConversionOptions): { [k: string]: any };

    /**
     * Converts this AnotherOne to JSON.
     * @returns JSON object
     */
    public toJSON(): { [k: string]: any };
}

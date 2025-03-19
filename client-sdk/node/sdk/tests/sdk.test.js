const sdk = require("../index");

test("SDK dummy method should return VertexSDK!", () => {
    expect(sdk.hello()).toBe("VertexSDK!");
});

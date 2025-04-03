alias VertexCacheSDK.Transport.TcpClient
alias VertexCacheSDK.Protocol.Command
alias VertexCacheSDK.Results.Result

defmodule Demo do
  def run do
    case TcpClient.connect() do
      {:ok, client} ->
        command_struct = Command.new("PING")
        command_str = Command.to_wire(command_struct)

        IO.puts(">>> Outgoing command: #{command_str}")

        payload = VertexCacheSDK.Crypto.Encrypt.maybe_encrypt(command_str)

        IO.puts(">>> Payload sent to server:")
        IO.inspect(payload)

        case TcpClient.send_command(client, command_str) do
          {:ok, response} ->
            parsed = Result.parse(response)
            IO.puts("Server Response: #{parsed.status} - #{String.trim(parsed.message)}")

          {:error, reason} ->
            IO.puts("Error sending command: #{inspect(reason)}")
        end

        TcpClient.close(client)

      {:error, reason} ->
        IO.puts("Connection failed: #{inspect(reason)}")
    end
  end
end

Demo.run()

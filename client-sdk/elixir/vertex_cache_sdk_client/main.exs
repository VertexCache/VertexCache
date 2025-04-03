# Run with: mix run vertex_cache_sdk_client/main.exs

alias VertexCacheSDK.Transport.TcpClient
alias VertexCacheSDK.Protocol.Command
alias VertexCacheSDK.Results.Result

defmodule Demo do
  def run do
    # Connect using .env
    case TcpClient.connect() do
      {:ok, client} ->
        # Build a command
        command = Command.new("PING") |> Command.to_wire()

        # Send the command
        case TcpClient.send_command(client, command) do
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
